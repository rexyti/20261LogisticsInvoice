package com.logistica.infrastructure.web.handlers;

import com.logistica.shared.exceptions.PaqueteNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaqueteNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(PaqueteNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", Instant.now().toString(),
                "error", "Paquete no encontrado",
                "idPaquete", ex.getIdPaquete().toString()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", Instant.now().toString(),
                "error", "Error interno del servidor",
                "mensaje", ex.getMessage() != null ? ex.getMessage() : "Sin detalle"
        ));
    }
}
