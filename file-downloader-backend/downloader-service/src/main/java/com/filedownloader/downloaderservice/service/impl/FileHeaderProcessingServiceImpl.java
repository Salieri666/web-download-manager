package com.filedownloader.downloaderservice.service.impl;

import com.filedownloader.corelib.utils.RetryUtils;
import com.filedownloader.corelib.utils.TransactionUtils;
import com.filedownloader.downloaderservice.db.repository.FileChunkRepository;
import com.filedownloader.downloaderservice.db.repository.FileDescriptionRepository;
import com.filedownloader.downloaderservice.model.entity.FileChunkEntity;
import com.filedownloader.downloaderservice.model.entity.FileDescriptionEntity;
import com.filedownloader.downloaderservice.model.enums.FileChunkStatus;
import com.filedownloader.downloaderservice.model.enums.FileDescriptionStatus;
import com.filedownloader.downloaderservice.service.FileHeaderProcessingService;
import com.filedownloader.exceptionlib.exception.BusinessException;
import com.filedownloader.exceptionlib.exception.EntityNotFoundException;
import com.filedownloader.exceptionlib.utils.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileHeaderProcessingServiceImpl implements FileHeaderProcessingService {

    private static final int CHUNK_PARTS = 10;
    private static final String ACCEPT_RANGES_BYTES = "bytes";
    private static final String USER_AGENT = "file-downloader-service";

    private final FileDescriptionRepository fileDescriptionRepository;
    private final FileChunkRepository fileChunkRepository;
    private final PlatformTransactionManager transactionManager;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    @Override
    public void process(UUID fileDescriptionId) {
        FileDescriptionContext context = RetryUtils.executeWithRetry(
                () -> markHeaderProcessing(fileDescriptionId),
                attempt -> log.warn("Lock attempt for getting fileDescriptionId={}, attempt {}", fileDescriptionId, attempt)
        );

        try {
            RemoteFileMetadata metadata = probeRemoteFile(context.sourceUrl());
            RetryUtils.executeWithRetry(() -> createChunks(context.fileDescriptionId(), metadata),
                    attempt -> log.warn("Lock attempt for creating chunks for fileDescriptionId={}, attempt {}", fileDescriptionId, attempt)
            );

            log.info(
                    "Header processing completed for fileDescriptionId={}, totalSize={}, rangeSupported={}, chunkCount={}",
                    fileDescriptionId,
                    metadata.totalSize(),
                    metadata.supportsRange(),
                    metadata.supportsRange() ? computeChunkCount(metadata.totalSize()) : 1
            );
        } catch (RuntimeException ex) {
            markFailed(fileDescriptionId, ex);
            throw ex;
        }
    }

    private FileDescriptionContext markHeaderProcessing(UUID fileDescriptionId) {
        return TransactionUtils.execute(transactionManager, () -> {
            FileDescriptionEntity fileDescription = fileDescriptionRepository.findByIdForUpdate(fileDescriptionId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            FileDescriptionEntity.class,
                            String.valueOf(fileDescriptionId)
                    ));
            fileDescription.setStatus(FileDescriptionStatus.HEADER_PROCESSING);
            return new FileDescriptionContext(fileDescription.getId(), fileDescription.getSourceUrl());
        });
    }

    private void markFailed(UUID fileDescriptionId, RuntimeException cause) {
        TransactionUtils.executeWithoutResult(transactionManager, () -> {
            fileDescriptionRepository.findByIdForUpdate(fileDescriptionId).ifPresent(fileDescription -> {
                fileDescription.setStatus(FileDescriptionStatus.FAILED);
                fileDescription.setErrorMessage(ExceptionUtils.getStackTraceAsString(cause));
            });
        });
    }

    private void createChunks(UUID fileDescriptionId, RemoteFileMetadata metadata) {
        TransactionUtils.executeWithoutResult(transactionManager, () -> {
            FileDescriptionEntity fileDescription = fileDescriptionRepository.findByIdForUpdate(fileDescriptionId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            FileDescriptionEntity.class,
                            String.valueOf(fileDescriptionId)
                    ));
            fileDescription.setTotalSize(metadata.totalSize());
            fileDescription.setMimeType(metadata.mimeType());
            fileDescription.setStatus(FileDescriptionStatus.DOWNLOADING_CHUNKS);
            fileDescription.setErrorMessage(null);

            fileDescription.getChunks().clear();
            List<FileChunkEntity> chunks = buildChunks(fileDescription, metadata);
            fileDescription.getChunks().addAll(chunks);
            fileChunkRepository.saveAll(chunks);
        });
    }

    private RemoteFileMetadata probeRemoteFile(String sourceUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(sourceUrl))
                    .timeout(Duration.ofSeconds(30))
                    .header("User-Agent", USER_AGENT)
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());

            if (response.statusCode() / 100 != 2) {
                throw new BusinessException("Failed to read remote file metadata from source url: " + sourceUrl
                        + ", status: " + response.statusCode());
            }

            long totalSize = response.headers()
                    .firstValueAsLong("Content-Length")
                    .orElseThrow(() -> new BusinessException(
                            "Remote file size is not available for source url: " + sourceUrl));
            String mimeType = response.headers()
                    .firstValue("Content-Type")
                    .orElse("application/octet-stream");
            boolean supportsRange = response.headers()
                    .firstValue("Accept-Ranges")
                    .map(value -> ACCEPT_RANGES_BYTES.equalsIgnoreCase(value.trim()))
                    .orElse(false);

            return new RemoteFileMetadata(totalSize, mimeType, supportsRange);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("Failed to probe remote file metadata for source url: " + sourceUrl, e);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid source url: " + sourceUrl, e);
        } catch (IOException e) {
            throw new BusinessException("Failed to probe remote file metadata for source url: " + sourceUrl, e);
        }
    }

    private List<FileChunkEntity> buildChunks(FileDescriptionEntity fileDescription, RemoteFileMetadata metadata) {
        if (metadata.totalSize() <= 0) {
            return List.of(createChunk(fileDescription, 0, 0L, 0L));
        }

        if (!metadata.supportsRange()) {
            return List.of(createChunk(fileDescription, 0, 0L, metadata.totalSize() - 1));
        }

        long chunkSize = Math.max(1L, (long) Math.ceil((double) metadata.totalSize() / CHUNK_PARTS));
        List<FileChunkEntity> chunks = new ArrayList<>();
        long start = 0L;
        int chunkIndex = 0;

        while (start < metadata.totalSize()) {
            long end = Math.min(start + chunkSize - 1, metadata.totalSize() - 1);
            chunks.add(createChunk(fileDescription, chunkIndex++, start, end));
            start = end + 1;
        }

        return chunks;
    }

    private int computeChunkCount(long totalSize) {
        if (totalSize <= 0) {
            return 1;
        }
        long chunkSize = Math.max(1L, (long) Math.ceil((double) totalSize / CHUNK_PARTS));
        return (int) Math.ceil((double) totalSize / chunkSize);
    }

    private FileChunkEntity createChunk(FileDescriptionEntity fileDescription, int chunkIndex, long startByte, long endByte) {
        return FileChunkEntity.builder()
                .fileDescription(fileDescription)
                .sourceUrl(fileDescription.getSourceUrl())
                .chunkIndex(chunkIndex)
                .startByte(startByte)
                .endByte(endByte)
                .currentSize(0L)
                .status(FileChunkStatus.PENDING)
                .retryCount(0)
                .errorMessage(null)
                .build();
    }

    private record FileDescriptionContext(UUID fileDescriptionId, String sourceUrl) {
    }

    private record RemoteFileMetadata(long totalSize, String mimeType, boolean supportsRange) {
    }
}
