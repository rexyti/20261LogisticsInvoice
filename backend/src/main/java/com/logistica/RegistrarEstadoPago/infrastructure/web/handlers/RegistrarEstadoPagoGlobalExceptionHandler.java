package com.logistica.RegistrarEstadoPago.infrastructure.web.handlers;

import com.logistica.RegistrarEstadoPago.exceptions.EstadoPagoInvalidoException;
import com.logistica.RegistrarEstadoPago.exceptions.RegistrarEstadoPagoEventoDuplicadoException;
import com.logistica.RegistrarEstadoPago.exceptions.EventoPagoNoProcesableException;
import com.logistica.RegistrarEstadoPago.exceptions.LiquidacionNoEncontradaException;
import com.logistica.RegistrarEstadoPago.exceptions.RegistrarEstadoPagoPagoNoEncontradoException;
import com.logistica.RegistrarEstadoPago.exceptions.TransicionEstadoPagoInvalidaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class RegistrarEstadoPagoGlobalExceptionHandler {

    @ExceptionHandler(RegistrarEstadoPagoPagoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handlePagoNotFound(RegistrarEstadoPagoPagoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", Instant.now().toString(),
                "error", "RegistrarEstadoPagoPago no encontrado",
                "mensaje", ex.getMessage()
        ));
    }

    @ExceptionHandler(LiquidacionNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleLiquidacionNotFound(LiquidacionNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", Instant.now().toString(),
                "error", "Liquidación no encontrada",
                "mensaje", ex.getMessage()
        ));
    }

    @ExceptionHandler(EstadoPagoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleEstadoPagoInvalido(EstadoPagoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", Instant.now().toString(),
                "error", "Estado de pago inválido",
                "mensaje", ex.getMessage()
        ));
    }

    @ExceptionHandler(TransicionEstadoPagoInvalidaException.class)
    public ResponseEntity<Map<String, Object>> handleTransicionInvalida(TransicionEstadoPagoInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp", Instant.now().toString(),
                "error", "Transición de estado inválida",
                "mensaje", ex.getMessage()
        ));
    }

    @ExceptionHandler(RegistrarEstadoPagoEventoDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleEventoDuplicado(RegistrarEstadoPagoEventoDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp", Instant.now().toString(),
                "error", "Evento duplicado",
                "mensaje", ex.getMessage()
        ));
    }

    @ExceptionHandler(EventoPagoNoProcesableException.class)
    public ResponseEntity<Map<String, Object>> handleEventoNoProcesable(EventoPagoNoProcesableException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of(
                "timestamp", Instant.now().toString(),
                "error", "Evento no procesable",
                "mensaje", ex.getMessage()
        ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        String mensaje = ex.getMessage() != null && ex.getMessage().contains("RegistrarEstadoPagoEstadoPagoEnum")
                ? "El estado de pago recibido no es soportado. Valores válidos: PENDIENTE, EN_PROCESO, PAGADO, RECHAZADO"
                : "El cuerpo de la solicitud no puede ser leído o contiene un valor inválido";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", Instant.now().toString(),
                "error", "estado invalido",
                "mensaje", mensaje
        ));
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handleOptimisticLock(OptimisticLockingFailureException ex) {
        log.warn("Conflicto de concurrencia", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp", Instant.now().toString(),
                "error", "Conflicto de concurrencia",
                "mensaje", "El estado fue modificado por otro proceso. Reintente la operación."
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", Instant.now().toString(),
                "error", "Solicitud inválida",
                "mensaje", ex.getBindingResult().getFieldErrors().stream()
                        .findFirst()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .orElse("La solicitud no cumple con el contrato esperado")
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Error interno no controlado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", Instant.now().toString(),
                "error", "Error interno del servidor",
                "mensaje", ex.getMessage() != null ? ex.getMessage() : "Sin detalle"
        ));
    }
}
