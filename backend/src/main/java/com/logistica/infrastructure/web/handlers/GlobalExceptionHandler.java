package com.logistica.infrastructure.web.handlers;

import com.logistica.domain.exceptions.ContratoNotFoundException;
import com.logistica.domain.exceptions.LiquidacionDuplicadaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ContratoNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleContratoNotFound(ContratoNotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LiquidacionDuplicadaException.class)
    public ResponseEntity<ProblemDetail> handleLiquidacionDuplicada(LiquidacionDuplicadaException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> handleIllegalState(IllegalStateException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneral(Exception ex) {
        return buildResponse("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ProblemDetail> buildResponse(String message, HttpStatus status) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(status, message);
        body.setTitle(status.getReasonPhrase());
        body.setProperty("timestamp", OffsetDateTime.now());

        return ResponseEntity.status(status).body(body);
    }
}