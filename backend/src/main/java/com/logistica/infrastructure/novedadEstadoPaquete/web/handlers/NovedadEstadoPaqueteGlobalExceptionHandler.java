package com.logistica.infrastructure.novedadEstadoPaquete.web.handlers;

import com.logistica.domain.novedadEstadoPaquete.exceptions.PaqueteNotFoundException;
import com.logistica.domain.novedadEstadoPaquete.exceptions.SincronizacionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class NovedadEstadoPaqueteGlobalExceptionHandler {

    @ExceptionHandler(PaqueteNotFoundException.class)
    public ResponseEntity<NovedadEstadoPaqueteErrorResponse> handleNotFound(PaqueteNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new NovedadEstadoPaqueteErrorResponse(404, "NOT_FOUND", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(SincronizacionException.class)
    public ResponseEntity<NovedadEstadoPaqueteErrorResponse> handleSincronizacion(SincronizacionException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new NovedadEstadoPaqueteErrorResponse(503, "SYNC_ERROR", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<NovedadEstadoPaqueteErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new NovedadEstadoPaqueteErrorResponse(500, "INTERNAL_ERROR", ex.getMessage(), LocalDateTime.now()));
    }
}
