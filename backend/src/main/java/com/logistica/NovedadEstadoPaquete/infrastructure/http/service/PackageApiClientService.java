package com.logistica.NovedadEstadoPaquete.infrastructure.http.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.NovedadEstadoPaquete.application.ports.PackageStatusGateway;
import com.logistica.NovedadEstadoPaquete.application.ports.PackageStatusResult;
import com.logistica.NovedadEstadoPaquete.infrastructure.http.clients.GestionClient;
import com.logistica.NovedadEstadoPaquete.infrastructure.http.dto.GestionPaqueteDTO;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Infrastructure adapter for PackageStatusGateway. Feign and Resilience4j stay
 * outside the application layer to preserve Clean Architecture dependencies.
 */
@Slf4j
@Service
public class PackageApiClientService implements PackageStatusGateway {

    private final GestionClient gestionClient;
    private final ObjectMapper objectMapper;
    private final Executor packageApiExecutor;

    public PackageApiClientService(
            GestionClient gestionClient,
            ObjectMapper objectMapper,
            @Qualifier("packageApiExecutor") Executor packageApiExecutor
    ) {
        this.gestionClient = gestionClient;
        this.objectMapper = objectMapper;
        this.packageApiExecutor = packageApiExecutor;
    }

    @Override
    @CircuitBreaker(name = "packageApi", fallbackMethod = "fallback")
    @Retry(name = "packageApi")
    @TimeLimiter(name = "packageApi")
    public CompletableFuture<PackageStatusResult> consultarEstado(Long idRuta, Long idPaquete) {
        return CompletableFuture.supplyAsync(() -> ejecutarConsulta(idRuta, idPaquete), packageApiExecutor);
    }

    private PackageStatusResult ejecutarConsulta(Long idRuta, Long idPaquete) {
        try {
            GestionPaqueteDTO dto = gestionClient.getEstadoPaquete(idRuta, idPaquete);
            String json = objectMapper.writeValueAsString(dto);
            return PackageStatusResult.exitoso(200, json, dto.estado());
        } catch (FeignException.NotFound e) {
            log.warn("NovedadEstadoPaquetePaquete no encontrado idRuta={} idPaquete={}", idRuta, idPaquete);
            return PackageStatusResult.noEncontrado(404, safeBody(e));
        } catch (FeignException e) {
            if (e.status() >= 500 || e.status() == -1) {
                throw e;
            }
            log.warn("Respuesta HTTP no exitosa {} al consultar paquete idRuta={} idPaquete={}", e.status(), idRuta, idPaquete);
            return PackageStatusResult.error(e.status(), safeBody(e));
        } catch (JsonProcessingException e) {
            return PackageStatusResult.error(-1, "Error serializando respuesta: " + e.getMessage());
        }
    }

    public CompletableFuture<PackageStatusResult> fallback(Long idRuta, Long idPaquete, Throwable ex) {
        Throwable cause = rootCause(ex);
        if (cause instanceof FeignException feignException) {
            log.error("Fallo HTTP persistente {} para idRuta={} idPaquete={}", feignException.status(), idRuta, idPaquete);
            return CompletableFuture.completedFuture(
                    PackageStatusResult.error(feignException.status(), safeBody(feignException))
            );
        }

        log.error("Fallback activado para idRuta={} idPaquete={} causa={}", idRuta, idPaquete, cause.getMessage());
        return CompletableFuture.completedFuture(
                PackageStatusResult.pendiente("Fallo de sincronización: " + cause.getMessage())
        );
    }

    private static Throwable rootCause(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }

    private static String safeBody(FeignException exception) {
        try {
            return exception.contentUTF8();
        } catch (RuntimeException ignored) {
            return exception.getMessage();
        }
    }
}
