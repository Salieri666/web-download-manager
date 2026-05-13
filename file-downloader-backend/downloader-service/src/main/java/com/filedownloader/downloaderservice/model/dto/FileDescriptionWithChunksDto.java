package com.filedownloader.downloaderservice.model.dto;

import com.filedownloader.downloaderservice.model.enums.FileDescriptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "File description response with chunks")
public class FileDescriptionWithChunksDto {

    private UUID id;
    private String filename;
    private String storagePath;
    private String sourceUrl;
    private FileDescriptionStatus status;
    private Long totalSize;
    private String mimeType;
    private String checksum;
    private Map<String, Object> metadata;
    private String errorMessage;
    private Instant createdDate;
    private Instant updatedDate;

    @Schema(description = "Percentage of downloading")
    private Integer percentage;

    @Schema(description = "File chunks")
    private List<FileChunkDto> chunks;
}
