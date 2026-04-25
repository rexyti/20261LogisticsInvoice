package com.logistica.application.usecases.paquete;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.application.dtos.response.HistorialEstadoResponseDTO;
import com.logistica.application.dtos.response.LogSincronizacionResponseDTO;
import com.logistica.application.dtos.response.SincronizacionResultadoDTO;
import com.logistica.application.ports.GestionPaquetePort;
import com.logistica.domain.enums.EstadoPaquete;
import com.logistica.domain.models.GestionPaquete;
import com.logistica.domain.models.HistorialEstado;
import com.logistica.domain.models.LogSincronizacion;
import com.logistica.domain.models.Paquete;
import com.logistica.domain.repositories.HistorialRepository;
import com.logistica.domain.repositories.LogSincronizacionRepository;
import com.logistica.domain.repositories.PaqueteRepository;
import com.logistica.domain.services.EstadoPaqueteService;
import com.logistica.shared.constants.AppConstants;
import com.logistica.shared.exceptions.PaqueteNoEncontradoException;
import com.logistica.shared.exceptions.PendienteSincronizacionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaqueteService implements SincronizarPaqueteUseCase, ObtenerHistorialUseCase, ObtenerLogsSincronizacionUseCase {

    private final GestionPaquetePort gestionPaquetePort;
    private final PaqueteRepository paqueteRepository;
    private final HistorialRepository historialRepository;
    private final LogSincronizacionRepository logRepository;
    private final EstadoPaqueteService estadoPaqueteService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public SincronizacionResultadoDTO sincronizarEstado(UUID idRuta, UUID idPaquete) {
        GestionPaquete respuesta;

        try {
            respuesta = gestionPaquetePort.consultarEstado(idRuta, idPaquete).get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            registrarLog(idPaquete, AppConstants.HTTP_ERROR_GENERICO, "Hilo interrumpido durante sincronización");
            marcarPendiente(idRuta, idPaquete);
            return SincronizacionResultadoDTO.pendiente(idPaquete.toString());
        } catch (ExecutionException | CompletionException ex) {
            return manejarErrorConsulta(idRuta, idPaquete, causaRaiz(ex));
        }

        registrarLog(idPaquete, AppConstants.HTTP_OK, toJson(respuesta));

        if (!idPaquete.toString().equalsIgnoreCase(respuesta.idPaquete())) {
            String mensaje = "El idPaquete recibido no coincide con el solicitado. solicitado="
                    + idPaquete + ", recibido=" + respuesta.idPaquete();
            log.warn(mensaje);
            registrarLog(idPaquete, AppConstants.HTTP_ERROR_GENERICO, mensaje);
            marcarPendiente(idRuta, idPaquete);
            return SincronizacionResultadoDTO.pendiente(idPaquete.toString());
        }

        return estadoPaqueteService.mapearEstado(respuesta.estado())
                .map(estado -> sincronizarEstadoValido(idRuta, idPaquete, estado))
                .orElseGet(() -> SincronizacionResultadoDTO.estadoNoMapeado(respuesta.estado()));
    }

    private SincronizacionResultadoDTO sincronizarEstadoValido(UUID idRuta, UUID idPaquete, EstadoPaquete estado) {
        Paquete paqueteActual = paqueteRepository.findById(idPaquete)
                .orElse(new Paquete(idPaquete, idRuta, estado));

        if (paqueteActual.tieneMismoEstado(estado)) {
            return SincronizacionResultadoDTO.exitoso(estado.getPorcentajePago(), estado.name());
        }

        Paquete paqueteActualizado = paqueteActual.actualizarEstado(estado);
        paqueteRepository.save(paqueteActualizado);
        historialRepository.save(new HistorialEstado(null, idPaquete, estado, Instant.now()));

        return SincronizacionResultadoDTO.exitoso(estado.getPorcentajePago(), estado.name());
    }

    private SincronizacionResultadoDTO manejarErrorConsulta(UUID idRuta, UUID idPaquete, Throwable causa) {
        if (causa instanceof PaqueteNoEncontradoException) {
            registrarLog(idPaquete, AppConstants.HTTP_NOT_FOUND, "Paquete no encontrado: " + idPaquete);
            return SincronizacionResultadoDTO.noEncontrado(idPaquete.toString());
        }

        if (causa instanceof PendienteSincronizacionException) {
            registrarLog(idPaquete, AppConstants.HTTP_ERROR_GENERICO,
                    "Fallo de sincronización: " + mensajeSeguro(causa));
            marcarPendiente(idRuta, idPaquete);
            return SincronizacionResultadoDTO.pendiente(idPaquete.toString());
        }

        registrarLog(idPaquete, AppConstants.HTTP_ERROR_GENERICO,
                "Error inesperado: " + mensajeSeguro(causa));
        marcarPendiente(idRuta, idPaquete);
        return SincronizacionResultadoDTO.pendiente(idPaquete.toString());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HistorialEstadoResponseDTO> obtenerHistorial(UUID idPaquete, Pageable pageable) {
        return historialRepository.findByIdPaquete(idPaquete, pageable)
                .map(h -> new HistorialEstadoResponseDTO(h.id(), h.idPaquete(), h.estado(), h.fecha()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogSincronizacionResponseDTO> obtenerLogs(UUID idPaquete) {
        return logRepository.findByIdPaquete(idPaquete).stream()
                .map(l -> new LogSincronizacionResponseDTO(
                        l.id(), l.idPaquete(), l.codigoRespuestaHTTP(), l.jsonRecibido(), l.timestamp()))
                .toList();
    }

    private void registrarLog(UUID idPaquete, int codigoHTTP, String json) {
        logRepository.save(new LogSincronizacion(null, idPaquete, codigoHTTP, json, Instant.now()));
    }

    private void marcarPendiente(UUID idRuta, UUID idPaquete) {
        Paquete paquete = paqueteRepository.findById(idPaquete)
                .map(p -> p.actualizarEstado(EstadoPaquete.PENDIENTE_SINCRONIZACION))
                .orElse(new Paquete(idPaquete, idRuta, EstadoPaquete.PENDIENTE_SINCRONIZACION));
        paqueteRepository.save(paquete);
    }

    private String toJson(GestionPaquete dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException ex) {
            return "{\"idPaquete\":\"" + dto.idPaquete() + "\",\"estado\":\"" + dto.estado() + "\"}";
        }
    }

    private Throwable causaRaiz(Throwable throwable) {
        Throwable actual = throwable;
        while (actual.getCause() != null
                && (actual instanceof ExecutionException || actual instanceof CompletionException)) {
            actual = actual.getCause();
        }
        return actual;
    }

    private String mensajeSeguro(Throwable throwable) {
        return throwable.getMessage() != null ? throwable.getMessage() : throwable.getClass().getSimpleName();
    }
}
