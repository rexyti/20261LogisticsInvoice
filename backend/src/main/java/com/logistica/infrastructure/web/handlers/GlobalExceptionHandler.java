package com.logistica.infrastructure.web.handlers;

import com.logistica.domain.exceptions.DomainException;
import com.logistica.domain.exceptions.RutaNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RutaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRutaNotFound(RutaNotFoundException e) {
        return ResponseEntity.status(404).body(
                new ErrorResponse(
                        e.getMessage(),
                        "RUTA_NOT_FOUND",
                        404,
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException e) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        e.getMessage(),
                        "DOMAIN_ERROR",
                        400,
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {

        // 🔥 log completo (CRÍTICO)
        log.error("Error inesperado", e);

        return ResponseEntity.internalServerError().body(
                new ErrorResponse(
                        "Error interno del servidor",
                        "INTERNAL_ERROR",
                        500,
                        LocalDateTime.now()
                )
        );
    }
}