package com.filedownloader.exceptionlib.handler;

import com.filedownloader.exceptionlib.exception.BusinessException;
import com.filedownloader.exceptionlib.exception.EntityNotFoundException;
import com.filedownloader.exceptionlib.model.ApiCommonError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Set;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException e, WebRequest request) {
        log.warn("Business exception: message={}, context={}", e.getMessage(), contextDetails(request), e);
        final var apiError = new ApiCommonError(
                HttpStatus.CONFLICT,
                e.getMessage(),
                contextDetails(request)
        );
        return ResponseEntity.status(HttpStatus.valueOf(apiError.getStatus())).body(apiError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e, WebRequest request) {
        log.warn("Entity not found: message={}, context={}", e.getMessage(), contextDetails(request), e);
        final var apiError = new ApiCommonError(
                HttpStatus.NOT_FOUND,
                e.getMessage(),
                contextDetails(request)
        );
        return ResponseEntity.status(HttpStatus.valueOf(apiError.getStatus())).body(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDeniedExceptionException(AccessDeniedException e, WebRequest request) {
        log.warn("Access denied: message={}, context={}", e.getMessage(), contextDetails(request), e);
        final var apiError = new ApiCommonError(
                HttpStatus.FORBIDDEN,
                e.getMessage(),
                contextDetails(request)
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnhandledException(Exception e, WebRequest request) {
        log.error("Unhandled exception: message={}, context={}", e.getMessage(), contextDetails(request), e);
        final var apiError = new ApiCommonError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                e.getMessage(),
                contextDetails(request)
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    private Set<String> contextDetails(WebRequest request) {
        String description = request.getDescription(false);
        String uri = description.startsWith("uri=") ? description.substring(4) : description;
        return Set.of("uri=" + uri);
    }
}
