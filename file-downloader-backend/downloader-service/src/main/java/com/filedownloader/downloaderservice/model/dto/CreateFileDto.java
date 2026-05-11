package com.filedownloader.downloaderservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "File description create request")
public class CreateFileDto {

    @NotBlank
    @Schema(description = "Original file name")
    private String filename;

    @NotBlank
    @Schema(description = "Source URL")
    private String sourceUrl;
}
