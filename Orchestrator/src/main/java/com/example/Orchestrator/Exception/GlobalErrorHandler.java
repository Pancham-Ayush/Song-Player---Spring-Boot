package com.example.Orchestrator.Exception;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public Mono<ResponseEntity<ApiError>> handleDuplicate(DuplicateKeyException ex) {
        return Mono.just(
                ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiError(ex.getMessage(), 409))
        );
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiError>> handleGeneric(Exception ex) {
        return Mono.just(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiError("Internal server error", 500))
        );
    }
}
