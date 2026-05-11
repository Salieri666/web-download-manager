package com.filedownloader.exceptionlib.exception;

import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(@NonNull Class<?> entityClass, String id) {
        super(composeMessage(entityClass, id));
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    private static String composeMessage(Class<?> entityClass, String id) {
        return String.format("%s was not found by id: %s", entityClass.getSimpleName(), id);
    }
}
