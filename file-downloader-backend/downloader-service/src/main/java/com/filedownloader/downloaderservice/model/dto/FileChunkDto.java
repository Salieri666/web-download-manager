package com.filedownloader.downloaderservice.model.dto;

import com.filedownloader.downloaderservice.model.enums.FileChunkStatus;

import java.time.Instant;
import java.util.UUID;

public record FileChunkDto(
        UUID id,
        UUID fileId,
        Integer chunkIndex,
        Long startByte,
        Long endByte,
        Long currentSize,
        FileChunkStatus status,
        String workerId,
        Integer retryCount,
        Instant lastHeartbeat,
        Instant completedAt
) {
}
