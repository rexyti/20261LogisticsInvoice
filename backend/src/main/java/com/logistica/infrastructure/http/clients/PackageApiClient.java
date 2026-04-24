package com.logistica.infrastructure.http.clients;

import com.logistica.infrastructure.http.dto.GestionPaqueteDTO;
import com.logistica.shared.exceptions.PaqueteNoEncontradoException;
import com.logistica.shared.exceptions.PendienteSincronizacionException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class PackageApiClient {

    private final GestionClient gestionClient;

    // Orden obligatorio según el plan: CircuitBreaker envuelve Retry, que envuelve TimeLimiter.
    // Con Spring AOP, la anotación más externa (CircuitBreaker) se aplica primero al método.
    @CircuitBreaker(name = "packageApi", fallbackMethod = "sincronizarEstadoFallback")
    @Retry(name = "packageApi")
    @TimeLimiter(name = "packageApi")
    public CompletableFuture<GestionPaqueteDTO> consultarEstado(UUID idRuta, UUID idPaquete) {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return gestionClient.getPackageState(idRuta.toString(), idPaquete.toString());
                    } catch (FeignException.NotFound e) {
                        // 404 no se reintenta: se convierte a dominio inmediatamente
                        throw new PaqueteNoEncontradoException(idPaquete);
                    }
                },
                Executors.newVirtualThreadPerTaskExecutor()
        );
    }

    // Fallback invocado por CircuitBreaker cuando todos los reintentos se agotan o el CB está abierto.
    // 404 (PaqueteNoEncontradoException) se propaga; cualquier otro fallo se marca como PENDIENTE.
    public CompletableFuture<GestionPaqueteDTO> sincronizarEstadoFallback(UUID idRuta, UUID idPaquete, Throwable t) {
        if (t instanceof PaqueteNoEncontradoException ex) {
            return CompletableFuture.failedFuture(ex);
        }
        return CompletableFuture.failedFuture(new PendienteSincronizacionException(idPaquete, t));
    }
}
