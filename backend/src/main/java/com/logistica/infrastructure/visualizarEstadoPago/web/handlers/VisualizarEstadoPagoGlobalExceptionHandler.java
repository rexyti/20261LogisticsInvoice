package com.logistica.infrastructure.visualizarEstadoPago.web.handlers;

import com.logistica.domain.visualizarEstadoPago.exceptions.AccessDeniedPaymentException;
import com.logistica.domain.visualizarEstadoPago.exceptions.VisualizarEstadoPagoPagoNoEncontradoException;
import com.logistica.domain.visualizarEstadoPago.exceptions.StorageUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class VisualizarEstadoPagoGlobalExceptionHandler {

    @ExceptionHandler(VisualizarEstadoPagoPagoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handlePagoNoEncontrado(
            VisualizarEstadoPagoPagoNoEncontradoException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorBody(ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedPaymentException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedPaymentException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(errorBody(ex.getMessage()));
    }

    @ExceptionHandler(StorageUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleStorageUnavailable(
            StorageUnavailableException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorBody("Sistema de almacenamiento no disponible"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobal(Exception ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody("Ocurrió un error inesperado"));
    }

    private Map<String, Object> errorBody(String message) {
        return Map.of("timestamp", LocalDateTime.now(), "message", message);
    }
}
