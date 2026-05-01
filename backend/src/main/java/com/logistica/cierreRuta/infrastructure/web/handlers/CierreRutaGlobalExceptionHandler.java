package com.logistica.cierreRuta.infrastructure.web.handlers;

import com.logistica.cierreRuta.domain.exceptions.CierreRutaDomainException;
import com.logistica.cierreRuta.domain.exceptions.CierreRutaEventoDuplicadoException;
import com.logistica.cierreRuta.domain.exceptions.ParadaInvalidaException;
import com.logistica.cierreRuta.domain.exceptions.RutaNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class CierreRutaGlobalExceptionHandler {

    @ExceptionHandler(RutaNotFoundException.class)
    public ResponseEntity<CierreRutaErrorResponse> handleRutaNotFound(RutaNotFoundException e) {
        return ResponseEntity.status(404).body(error(e.getMessage(), "RUTA_NOT_FOUND", 404));
    }

    @ExceptionHandler(CierreRutaEventoDuplicadoException.class)
    public ResponseEntity<CierreRutaErrorResponse> handleEventoDuplicado(CierreRutaEventoDuplicadoException e) {
        log.warn("Evento duplicado: {}", e.getMessage());
        return ResponseEntity.status(409).body(error(e.getMessage(), "EVENTO_DUPLICADO", 409));
    }

    @ExceptionHandler(ParadaInvalidaException.class)
    public ResponseEntity<CierreRutaErrorResponse> handleParadaInvalida(ParadaInvalidaException e) {
        log.warn("Parada inválida: {}", e.getMessage());
        return ResponseEntity.badRequest().body(error(e.getMessage(), "PARADA_INVALIDA", 400));
    }

    @ExceptionHandler(CierreRutaDomainException.class)
    public ResponseEntity<CierreRutaErrorResponse> handleDomainException(CierreRutaDomainException e) {
        return ResponseEntity.badRequest().body(error(e.getMessage(), "DOMAIN_ERROR", 400));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CierreRutaErrorResponse> handleGenericException(Exception e) {
        log.error("Error inesperado", e);
        return ResponseEntity.internalServerError()
                .body(error("Error interno del servidor", "INTERNAL_ERROR", 500));
    }

    private CierreRutaErrorResponse error(String mensaje, String codigo, int status) {
        return new CierreRutaErrorResponse(mensaje, codigo, status, LocalDateTime.now());
    }
}
