package com.logistica.contratos.infrastructure.web.handlers;

import com.logistica.contratos.domain.exceptions.ContratoInvalidoException;
import com.logistica.contratos.domain.exceptions.ContratosContratoNotFoundException;
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
public class ContratosGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ContratosErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errores.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ContratosErrorResponse.builder()
                        .mensaje("Error de validación en los campos del contrato")
                        .errores(errores)
                        .build());
    }

    @ExceptionHandler(ContratoYaExisteException.class)
    public ResponseEntity<ContratosErrorResponse> handleContratoYaExiste(ContratoYaExisteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ContratosErrorResponse.builder()
                        .mensaje("El contrato con este identificador ya existe")
                        .build());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ContratosErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ContratosErrorResponse.builder()
                        .mensaje("El contrato con este identificador ya existe")
                        .build());
    }

    @ExceptionHandler(ContratosContratoNotFoundException.class)
    public ResponseEntity<ContratosErrorResponse> handleContratoNotFound(ContratosContratoNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ContratosErrorResponse.builder()
                        .mensaje(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(TransportistaNotFoundException.class)
    public ResponseEntity<ContratosErrorResponse> handleTransportistaNotFound(TransportistaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ContratosErrorResponse.builder()
                        .mensaje(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ContratosErrorResponse> handleRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ContratosErrorResponse.builder()
                        .mensaje(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(ContratoInvalidoException.class)
    public ResponseEntity<ContratosErrorResponse> handleContratoInvalido(ContratoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ContratosErrorResponse.builder()
                        .mensaje(ex.getMessage())
                        .build());
    }
}
