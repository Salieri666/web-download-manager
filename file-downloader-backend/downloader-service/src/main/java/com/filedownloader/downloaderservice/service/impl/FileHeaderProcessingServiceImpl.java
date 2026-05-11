package com.filedownloader.downloaderservice.service.impl;

import com.filedownloader.downloaderservice.service.FileHeaderProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
public class FileHeaderProcessingServiceImpl implements FileHeaderProcessingService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    @Override
    public void process(UUID fileDescriptionId) {
        log.debug("Header processing is not implemented yet for fileDescriptionId={}", fileDescriptionId);
    }

    /*private RemoteFileMetadata probeRemoteFile(String sourceUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(sourceUrl))
                    .timeout(Duration.ofSeconds(30))
                    .header("User-Agent", "file-downloader-service")
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

    private FileChunkEntity createChunk(FileDescriptionEntity fileDescription, int chunkIndex, long startByte, long endByte) {
        return FileChunkEntity.builder()
                .fileDescription(fileDescription)
                .chunkIndex(chunkIndex)
                .startByte(startByte)
                .endByte(endByte)
                .currentSize(0L)
                .status(FileChunkStatus.PENDING)
                .retryCount(0)
                .errorMessage(null)
                .build();
    }


    private record RemoteFileMetadata(long totalSize, String mimeType, boolean supportsRange) {
    }*/
}
