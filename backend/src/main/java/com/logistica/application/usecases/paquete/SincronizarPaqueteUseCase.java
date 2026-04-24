package com.logistica.application.usecases.paquete;

import com.logistica.application.dtos.response.SincronizacionResultadoDTO;
import com.logistica.domain.enums.EstadoPaquete;
import com.logistica.domain.models.HistorialEstado;
import com.logistica.domain.models.LogSincronizacion;
import com.logistica.domain.models.Paquete;
import com.logistica.domain.repositories.HistorialRepository;
import com.logistica.domain.repositories.LogSincronizacionRepository;
import com.logistica.domain.repositories.PaqueteRepository;
import com.logistica.domain.services.EstadoPaqueteService;
import com.logistica.infrastructure.http.service.ApiCallResult;
import com.logistica.infrastructure.http.service.PackageApiClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletionException;

/**
 * Orchestrates the synchronous consultation of package status with the
 * Package Management Module. The query is triggered automatically by the
 * liquidation process — not manually by users.
 *
 * All DB writes (LogSincronizacion + Paquete + HistorialEstado) are atomic
 * via @Transactional: if any write fails, all three are rolled back.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SincronizarPaqueteUseCase {

    private final PackageApiClientService  packageApiClientService;
    private final PaqueteRepository        paqueteRepository;
    private final HistorialRepository      historialRepository;
    private final LogSincronizacionRepository logSincronizacionRepository;
    private final EstadoPaqueteService     estadoPaqueteService;

    @Transactional
    public SincronizacionResultadoDTO execute(Long idRuta, Long idPaquete) {

        // Step 1 – Call external API (HTTP, no DB transaction involved)
        ApiCallResult resultado = consultarApi(idRuta, idPaquete);

        // Step 2 – Persist audit log regardless of outcome (FR-003, SC-002)
        guardarLog(idPaquete, resultado);

        // Step 3 – Handle result variants
        if (resultado.isPendientePorSincronizacion()) {
            actualizarEstadoPaquete(idPaquete, idRuta, "PENDIENTE_SINCRONIZACION");
            return SincronizacionResultadoDTO.pendiente(idPaquete);
        }

        if (resultado.isPaqueteNoEncontrado()) {
            log.warn("Paquete no encontrado en módulo de gestión idPaquete={}", idPaquete);
            return SincronizacionResultadoDTO.noEncontrado(idPaquete);
        }

        if (!resultado.isExitoso()) {
            log.error("Respuesta HTTP {} para idPaquete={}", resultado.getCodigoRespuestaHTTP(), idPaquete);
            return SincronizacionResultadoDTO.error(idPaquete, resultado.getCodigoRespuestaHTTP());
        }

        // Step 3c – HTTP 200: validate state mapping (FR-002)
        Optional<EstadoPaquete> estadoOpt = estadoPaqueteService.resolverEstado(resultado.getEstadoRaw());
        if (estadoOpt.isEmpty()) {
            log.warn("Estado no mapeado '{}' para idPaquete={}", resultado.getEstadoRaw(), idPaquete);
            return SincronizacionResultadoDTO.estadoNoMapeado(idPaquete, resultado.getEstadoRaw());
        }

        EstadoPaquete estado = estadoOpt.get();

        // Step 3d – Update package + append history entry (SC-001, FR-004)
        actualizarEstadoPaquete(idPaquete, idRuta, estado.name());
        historialRepository.save(new HistorialEstado(null, idPaquete, estado.name(), LocalDateTime.now()));

        return SincronizacionResultadoDTO.exitoso(idPaquete, estado.name(),
                estadoPaqueteService.calcularPorcentajePago(estado));
    }

    private ApiCallResult consultarApi(Long idRuta, Long idPaquete) {
        try {
            return packageApiClientService.consultarEstado(idRuta, idPaquete).join();
        } catch (CompletionException ex) {
            log.error("Error inesperado consultando paquete idPaquete={}", idPaquete, ex);
            return ApiCallResult.pendiente(ex.getMessage());
        }
    }

    private void guardarLog(Long idPaquete, ApiCallResult resultado) {
        logSincronizacionRepository.save(new LogSincronizacion(
                null,
                idPaquete,
                resultado.getCodigoRespuestaHTTP(),
                resultado.getJsonRecibido(),
                LocalDateTime.now()
        ));
    }

    private void actualizarEstadoPaquete(Long idPaquete, Long idRuta, String estado) {
        Paquete paquete = paqueteRepository.findByIdPaquete(idPaquete)
                .orElse(new Paquete(idPaquete, idRuta, null, null));
        paquete.setEstadoActual(estado);
        paquete.setUpdatedAt(LocalDateTime.now());
        paqueteRepository.save(paquete);
    }
}
