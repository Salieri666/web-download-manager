package com.filedownloader.exceptionlib.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Common api error response")
public class ApiCommonError {

    private Instant timestamp = Instant.now();
    private int status;
    private String error;
    private String message;
    private Set<String> additional;

    public ApiCommonError(@NonNull HttpStatus status, String message) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
    }

    public ApiCommonError(@NonNull HttpStatus status, String message, Set<String> additional) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.additional = additional;
    }
}
