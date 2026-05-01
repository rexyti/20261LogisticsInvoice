package com.logistica.NovedadEstadoPaquete.application.usecases.paquete;

import com.logistica.NovedadEstadoPaquete.application.dtos.response.SincronizacionResultadoDTO;
import com.logistica.NovedadEstadoPaquete.application.ports.PackageStatusGateway;
import com.logistica.NovedadEstadoPaquete.application.ports.PackageStatusResult;
import com.logistica.NovedadEstadoPaquete.domain.enums.NovedadEstadoPaqueteEstadoPaquete;
import com.logistica.NovedadEstadoPaquete.domain.models.HistorialEstado;
import com.logistica.NovedadEstadoPaquete.domain.models.LogSincronizacion;
import com.logistica.NovedadEstadoPaquete.domain.models.NovedadEstadoPaquetePaquete;
import com.logistica.NovedadEstadoPaquete.domain.repositories.HistorialRepository;
import com.logistica.NovedadEstadoPaquete.domain.repositories.LogSincronizacionRepository;
import com.logistica.NovedadEstadoPaquete.domain.repositories.PaqueteRepository;
import com.logistica.NovedadEstadoPaquete.domain.services.EstadoPaqueteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletionException;
/**
 * Application use case. It depends only on domain repositories/services and on
 * the PackageStatusGateway port, not on Feign, WebClient or any infrastructure class.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SincronizarPaqueteUseCase {

    private static final String ESTADO_PENDIENTE_SINCRONIZACION = "PENDIENTE_SINCRONIZACION";

    private final PackageStatusGateway packageStatusGateway;
    private final PaqueteRepository paqueteRepository;
    private final HistorialRepository historialRepository;
    private final LogSincronizacionRepository logSincronizacionRepository;
    private final EstadoPaqueteService estadoPaqueteService;

    @Transactional
    public SincronizacionResultadoDTO execute(Long idRuta, Long idPaquete) {
        PackageStatusResult resultado = consultarApi(idRuta, idPaquete);
        guardarLog(idPaquete, resultado);

        if (resultado.pendientePorSincronizacion()) {
            actualizarEstadoPaquete(idPaquete, idRuta, ESTADO_PENDIENTE_SINCRONIZACION);
            return SincronizacionResultadoDTO.pendiente(idPaquete);
        }

        if (resultado.paqueteNoEncontrado()) {
            log.warn("NovedadEstadoPaquetePaquete no encontrado en módulo de gestión idPaquete={}", idPaquete);
            return SincronizacionResultadoDTO.noEncontrado(idPaquete);
        }

        if (!resultado.exitoso()) {
            log.error("Respuesta HTTP {} para idPaquete={}", resultado.codigoRespuestaHTTP(), idPaquete);
            return SincronizacionResultadoDTO.error(idPaquete, resultado.codigoRespuestaHTTP());
        }

        Optional<NovedadEstadoPaqueteEstadoPaquete> estadoOpt = estadoPaqueteService.resolverEstado(resultado.estadoRaw());
        if (estadoOpt.isEmpty()) {
            log.warn("Estado no mapeado '{}' para idPaquete={}", resultado.estadoRaw(), idPaquete);
            return SincronizacionResultadoDTO.estadoNoMapeado(idPaquete, resultado.estadoRaw());
        }

        NovedadEstadoPaqueteEstadoPaquete estado = estadoOpt.get();
        actualizarEstadoPaquete(idPaquete, idRuta, estado.name());
        historialRepository.save(new HistorialEstado(null, idPaquete, estado.name(), LocalDateTime.now()));

        return SincronizacionResultadoDTO.exitoso(
                idPaquete,
                estado.name(),
                estadoPaqueteService.calcularPorcentajePago(estado)
        );
    }

    private PackageStatusResult consultarApi(Long idRuta, Long idPaquete) {
        try {
            return packageStatusGateway.consultarEstado(idRuta, idPaquete).join();
        } catch (CompletionException ex) {
            log.error("Error inesperado consultando paquete idPaquete={}", idPaquete, ex);
            return PackageStatusResult.pendiente(ex.getMessage());
        }
    }

    private void guardarLog(Long idPaquete, PackageStatusResult resultado) {
        logSincronizacionRepository.save(new LogSincronizacion(
                null,
                idPaquete,
                resultado.codigoRespuestaHTTP(),
                resultado.jsonRecibido(),
                LocalDateTime.now()
        ));
    }

    private void actualizarEstadoPaquete(Long idPaquete, Long idRuta, String estado) {
        NovedadEstadoPaquetePaquete paquete = paqueteRepository.findByIdPaquete(idPaquete)
                .orElse(new NovedadEstadoPaquetePaquete(idPaquete, idRuta, null, null));
        paquete.setIdRuta(idRuta);
        paquete.setEstadoActual(estado);
        paquete.setUpdatedAt(LocalDateTime.now());
        paqueteRepository.save(paquete);
    }
}
