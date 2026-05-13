package com.filedownloader.corelib.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "File description")
public class FileDTO {

    private String fileName;
    private MediaType contentType;
    private Long contentLength;

    private InputStream inputStream;

    private Instant lastModified;

    public ResponseEntity<Resource> getResource() {
        InputStreamResource resource = new InputStreamResource(inputStream);

        String etag = "\"" + contentLength + "-" + lastModified + "\"";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        headers.setContentLength(contentLength); // Используем поле contentLength, а не длину массива
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(fileName, StandardCharsets.UTF_8)
                .build());
        headers.setAccessControlExposeHeaders(List.of(HttpHeaders.CONTENT_DISPOSITION));
        headers.setLastModified(lastModified);
        headers.setETag(etag);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}
