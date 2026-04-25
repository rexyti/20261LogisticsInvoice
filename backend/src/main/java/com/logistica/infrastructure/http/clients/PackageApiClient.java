package com.logistica.infrastructure.http.clients;

import com.logistica.application.ports.GestionPaquetePort;
import com.logistica.domain.models.GestionPaquete;
import com.logistica.infrastructure.http.dto.GestionPaqueteDTO;
import com.logistica.shared.exceptions.PaqueteNoEncontradoException;
import com.logistica.shared.exceptions.PendienteSincronizacionException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
public class PackageApiClient implements GestionPaquetePort {

    private final GestionClient gestionClient;

    @Qualifier("packageApiExecutor")
    private final ExecutorService packageApiExecutor;

    @Override
    @CircuitBreaker(name = "packageApi", fallbackMethod = "sincronizarEstadoFallback")
    @Retry(name = "packageApi")
    @TimeLimiter(name = "packageApi")
    public CompletableFuture<GestionPaquete> consultarEstado(UUID idRuta, UUID idPaquete) {
        return CompletableFuture.supplyAsync(
                () -> consultarEstadoSincronico(idRuta, idPaquete),
                packageApiExecutor
        );
    }

    private GestionPaquete consultarEstadoSincronico(UUID idRuta, UUID idPaquete) {
        try {
            GestionPaqueteDTO dto = gestionClient.getPackageState(idRuta.toString(), idPaquete.toString());
            return new GestionPaquete(dto.idPaquete(), dto.estado());
        } catch (FeignException.NotFound e) {
            throw new PaqueteNoEncontradoException(idPaquete);
        } catch (FeignException e) {
            throw new CompletionException(e);
        }
    }

    public CompletableFuture<GestionPaquete> sincronizarEstadoFallback(UUID idRuta, UUID idPaquete, Throwable t) {
        Throwable causa = causaRaiz(t);
        if (causa instanceof PaqueteNoEncontradoException ex) {
            return CompletableFuture.failedFuture(ex);
        }
        return CompletableFuture.failedFuture(new PendienteSincronizacionException(idPaquete, causa));
    }

    private Throwable causaRaiz(Throwable throwable) {
        Throwable actual = throwable;
        while (actual.getCause() != null
                && (actual instanceof CompletionException || actual.getClass().getName().contains("Execution"))) {
            actual = actual.getCause();
        }
        return actual;
    }
}
