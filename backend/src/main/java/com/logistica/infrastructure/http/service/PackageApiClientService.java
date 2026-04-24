package com.logistica.infrastructure.http.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.infrastructure.http.clients.GestionClient;
import com.logistica.infrastructure.http.dto.GestionPaqueteDTO;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Wraps GestionClient with Resilience4j in the mandatory order:
 * @CircuitBreaker (outermost) → @Retry → @TimeLimiter (innermost).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PackageApiClientService {

    private final GestionClient gestionClient;
    private final ObjectMapper  objectMapper;

    @CircuitBreaker(name = "packageApi", fallbackMethod = "fallback")
    @Retry(name = "packageApi")
    @TimeLimiter(name = "packageApi")
    public CompletableFuture<ApiCallResult> consultarEstado(Long idRuta, Long idPaquete) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                GestionPaqueteDTO dto = gestionClient.getEstadoPaquete(idRuta, idPaquete);
                String json = objectMapper.writeValueAsString(dto);
                return ApiCallResult.exitoso(200, json, dto.estado());
            } catch (FeignException.NotFound e) {
                log.warn("Paquete no encontrado idRuta={} idPaquete={}", idRuta, idPaquete);
                return ApiCallResult.noEncontrado(404, e.contentUTF8());
            } catch (FeignException e) {
                log.error("Error HTTP {} al consultar paquete idRuta={} idPaquete={}", e.status(), idRuta, idPaquete);
                return ApiCallResult.error(e.status(), e.contentUTF8());
            } catch (JsonProcessingException e) {
                return ApiCallResult.error(-1, "Error serializando respuesta: " + e.getMessage());
            }
        });
    }

    // Fallback: handles TimeoutException (after 3 retries) and CircuitBreaker open state
    public CompletableFuture<ApiCallResult> fallback(Long idRuta, Long idPaquete, Throwable ex) {
        log.error("Fallback activado para idRuta={} idPaquete={} causa={}", idRuta, idPaquete, ex.getMessage());
        return CompletableFuture.completedFuture(
                ApiCallResult.pendiente("Fallo de sincronización: " + ex.getMessage())
        );
    }
}
