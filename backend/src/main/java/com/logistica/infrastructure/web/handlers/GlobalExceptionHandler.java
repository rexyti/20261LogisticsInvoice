package com.logistica.infrastructure.web.handlers;

import com.logistica.domain.exceptions.DomainException;
import com.logistica.domain.exceptions.RutaNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RutaNotFoundException.class)
    public ResponseEntity<Void> handleRutaNotFound(RutaNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, String>> handleDomainException(DomainException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor"));
    }
}
