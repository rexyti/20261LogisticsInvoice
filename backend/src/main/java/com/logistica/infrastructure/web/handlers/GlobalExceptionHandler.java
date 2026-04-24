package com.logistica.infrastructure.web.handlers;

import com.logistica.domain.exceptions.AccesoDenegadoException;
import com.logistica.domain.exceptions.LiquidacionAunNoCalculadaException;
import com.logistica.domain.exceptions.LiquidacionNoEncontradaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(LiquidacionNoEncontradaException.class)
    public ResponseEntity<ErrorResponseDTO> handleLiquidacionNoEncontrada(LiquidacionNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO("LIQUIDACION_NO_ENCONTRADA", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(LiquidacionAunNoCalculadaException.class)
    public ResponseEntity<ErrorResponseDTO> handleLiquidacionAunNoCalculada(LiquidacionAunNoCalculadaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO("LIQUIDACION_AUN_NO_CALCULADA", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccesoDenegado(AccesoDenegadoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDTO("ACCESO_DENEGADO", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleSpringAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDTO("ACCESO_DENEGADO",
                        "No tiene permisos para realizar esta accion.", LocalDateTime.now()));
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<ErrorResponseDTO> handleStorageFailure(DataAccessResourceFailureException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponseDTO("SISTEMA_NO_DISPONIBLE",
                        "El sistema de almacenamiento no esta disponible temporalmente. Intente nuevamente mas tarde.",
                        LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO("PARAMETROS_INVALIDOS", mensaje, LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception ex) {
        log.error("Error inesperado no manejado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO("ERROR_INTERNO",
                        "Ha ocurrido un error inesperado. Contacte al administrador.", LocalDateTime.now()));
    }

    public record ErrorResponseDTO(String codigo, String mensaje, LocalDateTime timestamp) {}
}
