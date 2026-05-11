package com.filedownloader.downloaderservice.model.dto;

import com.filedownloader.downloaderservice.model.enums.FileDescriptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record FileDescriptionDto(
        UUID id,
        @NotBlank
        @Schema(description = "Original file name")
        String filename,
        @NotBlank
        @Schema(description = "Local path or S3 key")
        String storagePath,
        @NotBlank
        @Schema(description = "Source URL")
        String sourceUrl,
        @Schema(description = "File description status")
        FileDescriptionStatus status,
        @NotNull
        @Schema(description = "Total file size in bytes")
        Long totalSize,
        @NotBlank
        @Schema(description = "MIME type")
        String mimeType,
        @Schema(description = "Checksum of whole file")
        String checksum,
        @Schema(description = "Additional metadata")
        Map<String, Object> metadata,
        Instant createdAt,
        Instant updatedAt
) {
}
