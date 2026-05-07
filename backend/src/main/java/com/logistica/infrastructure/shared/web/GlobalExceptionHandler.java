package com.logistica.infrastructure.shared.web;

import com.logistica.domain.cierreRuta.exceptions.EventoDuplicadoException;
import com.logistica.domain.cierreRuta.exceptions.ParadaInvalidaException;
import com.logistica.domain.cierreRuta.exceptions.RutaNotFoundException;
import com.logistica.domain.contratos.exceptions.ContratoInvalidoException;
import com.logistica.domain.contratos.exceptions.ContratoNotFoundException;
import com.logistica.domain.contratos.exceptions.ContratoYaExisteException;
import com.logistica.domain.contratos.exceptions.RecursoNoEncontradoException;
import com.logistica.domain.contratos.exceptions.TransportistaNotFoundException;
import com.logistica.domain.liquidacion.exceptions.ContratoTarifaNoEncontradaException;
import com.logistica.domain.liquidacion.exceptions.DuplicadaException;
import com.logistica.domain.liquidacion.exceptions.NotFoundException;
import com.logistica.domain.novedadEstadoPaquete.exceptions.PaqueteNotFoundException;
import com.logistica.domain.novedadEstadoPaquete.exceptions.SincronizacionException;
import com.logistica.domain.registrarEstadoPago.exceptions.EstadoPagoInvalidoException;
import com.logistica.domain.registrarEstadoPago.exceptions.EventoPagoNoProcesableException;
import com.logistica.domain.registrarEstadoPago.exceptions.RegistrarEstadoPagoEventoDuplicadoException;
import com.logistica.domain.registrarEstadoPago.exceptions.RegistrarEstadoPagoPagoNoEncontradoException;
import com.logistica.domain.registrarEstadoPago.exceptions.TransicionEstadoPagoInvalidaException;
import com.logistica.domain.shared.exceptions.DomainException;
import com.logistica.domain.visualizarEstadoPago.exceptions.AccessDeniedPaymentException;
import com.logistica.domain.visualizarEstadoPago.exceptions.StorageUnavailableException;
import com.logistica.domain.visualizarEstadoPago.exceptions.VisualizarEstadoPagoPagoNoEncontradoException;
import com.logistica.domain.visualizarLiquidacion.exceptions.AccesoDenegadoException;
import com.logistica.domain.visualizarLiquidacion.exceptions.LiquidacionAunNoCalculadaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 400 Bad Request ──────────────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> detalles = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            detalles.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.badRequest()
                .body(ApiError.of("VALIDACION_FALLIDA", "Error de validación en los campos", detalles));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex) {
        String msg = ex.getMessage() != null && ex.getMessage().contains("Enum")
                ? "Valor de enumeración no soportado en la solicitud"
                : "El cuerpo de la solicitud no puede ser leído o contiene un valor inválido";
        return ResponseEntity.badRequest().body(ApiError.of("SOLICITUD_INVALIDA", msg));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiError> handleIllegal(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ApiError.of("SOLICITUD_INVALIDA", ex.getMessage()));
    }

    @ExceptionHandler({ParadaInvalidaException.class, ContratoInvalidoException.class,
            EstadoPagoInvalidoException.class})
    public ResponseEntity<ApiError> handleDomainBadRequest(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ApiError.of("ERROR_DOMINIO", ex.getMessage()));
    }

    @ExceptionHandler(EventoPagoNoProcesableException.class)
    public ResponseEntity<ApiError> handleNoProcesable(EventoPagoNoProcesableException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiError.of("EVENTO_NO_PROCESABLE", ex.getMessage()));
    }

    @ExceptionHandler(LiquidacionAunNoCalculadaException.class)
    public ResponseEntity<ApiError> handleAunNoCalculada(LiquidacionAunNoCalculadaException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiError.of("LIQUIDACION_AUN_NO_CALCULADA", ex.getMessage()));
    }

    // ── 403 Forbidden ────────────────────────────────────────────────────────

    @ExceptionHandler({AccessDeniedException.class, AccesoDenegadoException.class,
            AccessDeniedPaymentException.class})
    public ResponseEntity<ApiError> handleAccessDenied(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiError.of("ACCESO_DENEGADO", "No tiene permisos para realizar esta acción"));
    }

    // ── 404 Not Found ────────────────────────────────────────────────────────

    @ExceptionHandler({RutaNotFoundException.class, ContratoNotFoundException.class,
            TransportistaNotFoundException.class, RecursoNoEncontradoException.class,
            ContratoTarifaNoEncontradaException.class, NotFoundException.class,
            PaqueteNotFoundException.class, RegistrarEstadoPagoPagoNoEncontradoException.class,
            VisualizarEstadoPagoPagoNoEncontradoException.class})
    public ResponseEntity<ApiError> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of("RECURSO_NO_ENCONTRADO", ex.getMessage()));
    }

    // LiquidacionNoEncontradaException exists in two packages — handle via FQN
    @ExceptionHandler({
            com.logistica.domain.registrarEstadoPago.exceptions.LiquidacionNoEncontradaException.class,
            com.logistica.domain.visualizarLiquidacion.exceptions.LiquidacionNoEncontradaException.class
    })
    public ResponseEntity<ApiError> handleLiquidacionNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of("LIQUIDACION_NO_ENCONTRADA", ex.getMessage()));
    }

    // ── 409 Conflict ─────────────────────────────────────────────────────────

    @ExceptionHandler({EventoDuplicadoException.class, DuplicadaException.class,
            ContratoYaExisteException.class, RegistrarEstadoPagoEventoDuplicadoException.class,
            TransicionEstadoPagoInvalidaException.class})
    public ResponseEntity<ApiError> handleConflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("CONFLICTO", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("CONFLICTO_DATOS", "El recurso ya existe o viola una restricción de integridad"));
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiError> handleOptimisticLock(OptimisticLockingFailureException ex) {
        log.warn("Conflicto de concurrencia detectado", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("CONFLICTO_CONCURRENCIA", "El recurso fue modificado por otro proceso. Reintente la operación."));
    }

    // ── 503 Service Unavailable ───────────────────────────────────────────────

    @ExceptionHandler({SincronizacionException.class, StorageUnavailableException.class,
            DataAccessResourceFailureException.class})
    public ResponseEntity<ApiError> handleServiceUnavailable(RuntimeException ex) {
        log.error("Servicio no disponible", ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiError.of("SISTEMA_NO_DISPONIBLE", "El sistema no está disponible temporalmente. Intente nuevamente."));
    }

    // ── Dominio genérico (base para excepciones con HttpStatus) ───────────────

    @ExceptionHandler(com.logistica.domain.liquidacion.exceptions.DomainException.class)
    public ResponseEntity<ApiError> handleLiquidacionDomain(
            com.logistica.domain.liquidacion.exceptions.DomainException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(ApiError.of("ERROR_DOMINIO", ex.getMessage()));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> handleDomain(DomainException ex) {
        return ResponseEntity.badRequest().body(ApiError.of("ERROR_DOMINIO", ex.getMessage()));
    }

    // ── 500 Catch-all ────────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        log.error("Error interno no controlado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.of("ERROR_INTERNO", "Ha ocurrido un error inesperado. Contacte al administrador."));
    }
}
