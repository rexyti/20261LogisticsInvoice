package com.logistica.infrastructure.web.handlers;

import com.logistica.domain.exceptions.PaqueteNotFoundException;
import com.logistica.domain.exceptions.SincronizacionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaqueteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(PaqueteNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, "NOT_FOUND", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(SincronizacionException.class)
    public ResponseEntity<ErrorResponse> handleSincronizacion(SincronizacionException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(503, "SYNC_ERROR", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "INTERNAL_ERROR", ex.getMessage(), LocalDateTime.now()));
    }
}
