package com.filedownloader.downloaderservice.service.impl;

import com.filedownloader.corelib.utils.TransactionUtils;
import com.filedownloader.downloaderservice.db.repository.FileChunkRepository;
import com.filedownloader.downloaderservice.model.entity.FileChunkEntity;
import com.filedownloader.downloaderservice.model.enums.FileChunkStatus;
import com.filedownloader.downloaderservice.service.FileChunkProcessingService;
import com.filedownloader.exceptionlib.exception.BusinessException;
import com.filedownloader.exceptionlib.utils.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileChunkProcessingServiceImpl implements FileChunkProcessingService {

    private static final String USER_AGENT = "file-downloader-service";
    private static final Duration HEARTBEAT_INTERVAL = Duration.ofSeconds(5);
    private static final Path TEMP_DOWNLOADS_DIR = Paths.get("temporary-downloads");

    private final FileChunkRepository fileChunkRepository;
    private final PlatformTransactionManager transactionManager;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(15))
            .build();
    private final String workerId = resolveWorkerId();

    @Override
    public void process(UUID fileChunkId) {
        ChunkProcessingContext context = TransactionUtils.execute(transactionManager, () -> {
            FileChunkEntity fileChunk = fileChunkRepository.getEntityById(fileChunkId);
            Instant now = Instant.now();
            fileChunk.setWorkerId(workerId);
            fileChunk.setLastHeartbeat(now);
            return new ChunkProcessingContext(
                    fileChunk.getId(),
                    fileChunk.getFileDescription().getId(),
                    fileChunk.getSourceUrl(),
                    fileChunk.getChunkIndex(),
                    fileChunk.getStartByte(),
                    fileChunk.getEndByte()
            );
        });
        Path tempFilePath = buildTempFilePath(context);

        try {
            long bytesWritten = downloadChunk(context, tempFilePath);
            completeChunk(context.fileChunkId(), tempFilePath, bytesWritten);
            log.info(
                    "Chunk processing completed for fileChunkId={}, tempFilePath={}",
                    fileChunkId,
                    tempFilePath
            );
        } catch (RuntimeException ex) {
            markFailed(context.fileChunkId(), ex);
            cleanupTempFile(tempFilePath);
            throw ex;
        }
    }

    private long downloadChunk(ChunkProcessingContext context, Path tempFilePath) {
        try {
            Files.createDirectories(tempFilePath.getParent());

            HttpRequest request = HttpRequest.newBuilder(URI.create(context.sourceUrl()))
                    .timeout(Duration.ofMinutes(10))
                    .header("User-Agent", USER_AGENT)
                    .header("Range", "bytes=%d-%d".formatted(context.startByte(), context.endByte()))
                    .GET()
                    .build();
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() != 200 && response.statusCode() != 206) {
                throw new BusinessException(
                        "Failed to download chunk " + context.fileChunkId() + ", status: " + response.statusCode()
                );
            }

            long bytesWritten;
            try (InputStream inputStream = response.body();
                 OutputStream outputStream = Files.newOutputStream(
                         tempFilePath,
                         StandardOpenOption.CREATE,
                         StandardOpenOption.TRUNCATE_EXISTING,
                         StandardOpenOption.WRITE
                 )) {
                bytesWritten = copyWithHeartbeat(context.fileChunkId(), inputStream, outputStream);
            }

            long expectedSize = context.expectedSize();
            if (bytesWritten != expectedSize) {
                throw new BusinessException(
                        "Downloaded chunk size mismatch for fileChunkId=" + context.fileChunkId()
                                + ", expected=" + expectedSize
                                + ", actual=" + bytesWritten
                );
            }

            return bytesWritten;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("Chunk download was interrupted for fileChunkId=" + context.fileChunkId(), e);
        } catch (IOException e) {
            throw new BusinessException("Failed to download chunk for fileChunkId=" + context.fileChunkId(), e);
        }
    }

    private long copyWithHeartbeat(UUID fileChunkId, InputStream inputStream, OutputStream outputStream)
            throws IOException {
        byte[] buffer = new byte[8192];
        long bytesWritten = 0L;
        Instant lastHeartbeatUpdate = Instant.now();
        int read;

        while ((read = inputStream.read(buffer)) >= 0) {
            if (read == 0) {
                continue;
            }

            outputStream.write(buffer, 0, read);
            bytesWritten += read;

            Instant now = Instant.now();
            if (Duration.between(lastHeartbeatUpdate, now).compareTo(HEARTBEAT_INTERVAL) >= 0) {
                updateHeartbeat(fileChunkId, bytesWritten, now);
                lastHeartbeatUpdate = now;
            }
        }

        updateHeartbeat(fileChunkId, bytesWritten, Instant.now());
        return bytesWritten;
    }

    private void updateHeartbeat(UUID fileChunkId, long currentSize, Instant heartbeat) {
        TransactionUtils.executeWithoutResult(transactionManager, () -> {
            FileChunkEntity fileChunk = fileChunkRepository.getEntityById(fileChunkId);
            fileChunk.setWorkerId(workerId);
            fileChunk.setCurrentSize(currentSize);
            fileChunk.setLastHeartbeat(heartbeat);
        });
    }

    private void completeChunk(UUID fileChunkId, Path tempFilePath, long bytesWritten) {
        TransactionUtils.executeWithoutResult(transactionManager, () -> {
            FileChunkEntity fileChunk = fileChunkRepository.getEntityById(fileChunkId);
            fileChunk.setWorkerId(workerId);
            fileChunk.setStoragePath(tempFilePath.toString());
            fileChunk.setCurrentSize(bytesWritten);
            fileChunk.setStatus(FileChunkStatus.COMPLETED);
            fileChunk.setLastHeartbeat(Instant.now());
            fileChunk.setCompletedAt(Instant.now());
            fileChunk.setErrorMessage(null);
        });
    }

    private void markFailed(UUID fileChunkId, RuntimeException cause) {
        TransactionUtils.executeWithoutResult(transactionManager, () -> {
            FileChunkEntity fileChunk = fileChunkRepository.getEntityById(fileChunkId);
            fileChunk.setWorkerId(workerId);
            fileChunk.setStatus(FileChunkStatus.FAILED);
            fileChunk.setErrorMessage(ExceptionUtils.getStackTraceAsString(cause));
            fileChunk.setRetryCount(fileChunk.getRetryCount() + 1);
            fileChunk.setLastHeartbeat(Instant.now());
        });
    }

    private Path buildTempFilePath(ChunkProcessingContext context) {
        return TEMP_DOWNLOADS_DIR
                .resolve(context.fileDescriptionId().toString())
                .resolve(context.fileChunkId() + "_" + context.chunkIndex() + ".temp");
    }

    private void cleanupTempFile(Path tempFilePath) {
        try {
            Files.deleteIfExists(tempFilePath);
        } catch (IOException e) {
            log.warn("Failed to cleanup temp file {}", tempFilePath, e);
        }
    }

    private String resolveWorkerId() {
        String host = System.getenv("HOSTNAME");
        if (host == null || host.isBlank()) {
            host = System.getenv("COMPUTERNAME");
        }
        if (host == null || host.isBlank()) {
            try {
                host = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                host = "unknown-host";
            }
        }
        return host + ":" + ManagementFactory.getRuntimeMXBean().getName();
    }

    private record ChunkProcessingContext(
            UUID fileChunkId,
            UUID fileDescriptionId,
            String sourceUrl,
            Integer chunkIndex,
            Long startByte,
            Long endByte
    ) {
        long expectedSize() {
            return endByte - startByte + 1;
        }
    }

}
