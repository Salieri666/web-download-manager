package com.filedownloader.downloaderservice.model.dto;

import com.filedownloader.downloaderservice.model.enums.FileDescriptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "File description response")
public class FileDescriptionDto {

    private UUID id;

    @NotBlank
    @Schema(description = "Original file name")
    private String filename;

    @NotBlank
    @Schema(description = "Local path or S3 key")
    private String storagePath;

    @NotBlank
    @Schema(description = "Source URL")
    private String sourceUrl;

    @Schema(description = "File description status")
    private FileDescriptionStatus status;

    @NotNull
    @Schema(description = "Total file size in bytes")
    private Long totalSize;

    @NotBlank
    @Schema(description = "MIME type")
    private String mimeType;

    @Schema(description = "Checksum of whole file")
    private String checksum;

    @Schema(description = "Additional metadata")
    private Map<String, Object> metadata;

    @Schema(description = "Error message")
    private String errorMessage;

    @Schema(description = "Percentage of downloading")
    private Integer percentage;

    private Instant createdDate;
    private Instant updatedDate;
}
