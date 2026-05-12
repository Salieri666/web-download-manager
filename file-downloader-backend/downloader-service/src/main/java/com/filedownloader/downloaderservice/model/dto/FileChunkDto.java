package com.filedownloader.downloaderservice.model.dto;

import com.filedownloader.downloaderservice.model.enums.FileChunkStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "File chunk response")
public class FileChunkDto {

    private UUID id;
    private UUID fileId;
    private Integer chunkIndex;
    private Long startByte;
    private Long endByte;
    private Long currentSize;
    private FileChunkStatus status;
    private String workerId;
    private String sourceUrl;
    private String storagePath;
    private Integer retryCount;
    private Instant lastHeartbeat;
    private Instant completedAt;
    private String errorMessage;
}
