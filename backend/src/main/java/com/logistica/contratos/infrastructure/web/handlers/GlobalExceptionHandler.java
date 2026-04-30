package com.logistica.contratos.infrastructure.web.handlers;

import com.logistica.contratos.domain.exceptions.ContratoInvalidoException;
import com.logistica.contratos.domain.exceptions.ContratoNotFoundException;
import com.logistica.contratos.domain.exceptions.ContratoYaExisteException;
import com.logistica.contratos.domain.exceptions.RecursoNoEncontradoException;
import com.logistica.contratos.domain.exceptions.TransportistaNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errores.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .mensaje("Error de validación en los campos del contrato")
                        .errores(errores)
                        .build());
    }

    @ExceptionHandler(ContratoYaExisteException.class)
    public ResponseEntity<ErrorResponse> handleContratoYaExiste(ContratoYaExisteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .mensaje("El contrato con este identificador ya existe")
                        .build());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .mensaje("El contrato con este identificador ya existe")
                        .build());
    }

    @ExceptionHandler(ContratoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleContratoNotFound(ContratoNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .mensaje(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(TransportistaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTransportistaNotFound(TransportistaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .mensaje(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .mensaje(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(ContratoInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleContratoInvalido(ContratoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .mensaje(ex.getMessage())
                        .build());
    }
}
