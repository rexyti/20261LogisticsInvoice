package com.logistica.infrastructure.cierreRuta.web.handlers;

import com.logistica.domain.shared.exceptions.DomainException;
import com.logistica.domain.cierreRuta.exceptions.EventoDuplicadoException;
import com.logistica.domain.cierreRuta.exceptions.ParadaInvalidaException;
import com.logistica.domain.cierreRuta.exceptions.RutaNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class CierreRutaGlobalExceptionHandler {

    @ExceptionHandler(RutaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRutaNotFound(RutaNotFoundException e) {
        return ResponseEntity.status(404).body(error(e.getMessage(), "RUTA_NOT_FOUND", 404));
    }

    @ExceptionHandler(EventoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleEventoDuplicado(EventoDuplicadoException e) {
        log.warn("Evento duplicado: {}", e.getMessage());
        return ResponseEntity.status(409).body(error(e.getMessage(), "EVENTO_DUPLICADO", 409));
    }

    @ExceptionHandler(ParadaInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleParadaInvalida(ParadaInvalidaException e) {
        log.warn("Parada inválida: {}", e.getMessage());
        return ResponseEntity.badRequest().body(error(e.getMessage(), "PARADA_INVALIDA", 400));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException e) {
        return ResponseEntity.badRequest().body(error(e.getMessage(), "DOMAIN_ERROR", 400));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Error inesperado", e);
        return ResponseEntity.internalServerError()
                .body(error("Error interno del servidor", "INTERNAL_ERROR", 500));
    }

    private ErrorResponse error(String mensaje, String codigo, int status) {
        return new ErrorResponse(mensaje, codigo, status, LocalDateTime.now());
    }
}
