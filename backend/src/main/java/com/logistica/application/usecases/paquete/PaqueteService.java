package com.logistica.application.usecases.paquete;

import com.logistica.application.dtos.response.HistorialEstadoResponseDTO;
import com.logistica.application.dtos.response.LogSincronizacionResponseDTO;
import com.logistica.application.dtos.response.SincronizacionResultadoDTO;
import com.logistica.domain.enums.EstadoPaquete;
import com.logistica.domain.models.HistorialEstado;
import com.logistica.domain.models.LogSincronizacion;
import com.logistica.domain.models.Paquete;
import com.logistica.domain.repositories.HistorialRepository;
import com.logistica.domain.repositories.LogSincronizacionRepository;
import com.logistica.domain.repositories.PaqueteRepository;
import com.logistica.infrastructure.http.clients.PackageApiClient;
import com.logistica.infrastructure.http.dto.GestionPaqueteDTO;
import com.logistica.infrastructure.http.mappers.GestionPaqueteMapper;
import com.logistica.shared.constants.AppConstants;
import com.logistica.shared.exceptions.PaqueteNoEncontradoException;
import com.logistica.shared.exceptions.PendienteSincronizacionException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class PaqueteService implements SincronizarPaqueteUseCase, ObtenerHistorialUseCase, ObtenerLogsSincronizacionUseCase {

    private final PackageApiClient packageApiClient;
    private final PaqueteRepository paqueteRepository;
    private final HistorialRepository historialRepository;
    private final LogSincronizacionRepository logRepository;
    private final GestionPaqueteMapper gestionPaqueteMapper;

    /**
     * Sincroniza el estado de un paquete consultando al Módulo de Gestión.
     * Toda la interacción con BD es atómica: si cualquier operación falla, todas se revierten (SC-001, FR-003).
     */
    @Override
    @Transactional
    public SincronizacionResultadoDTO sincronizarEstado(UUID idRuta, UUID idPaquete) {
        GestionPaqueteDTO respuesta = null;
        int httpStatus = AppConstants.HTTP_ERROR_GENERICO;
        String jsonRecibido = null;

        // Paso 1: consulta HTTP sincrónica al Módulo de Gestión
        try {
            respuesta = packageApiClient.consultarEstado(idRuta, idPaquete).get();
            httpStatus = AppConstants.HTTP_OK;
            jsonRecibido = toJson(respuesta);

        } catch (ExecutionException ex) {
            Throwable causa = ex.getCause();

            // Paquete inexistente (FR-002, Scenario 2): registrar y detener este paquete
            if (causa instanceof PaqueteNoEncontradoException) {
                httpStatus = AppConstants.HTTP_NOT_FOUND;
                jsonRecibido = "Paquete no encontrado: " + idPaquete;
                registrarLog(idPaquete, httpStatus, jsonRecibido);
                return SincronizacionResultadoDTO.noEncontrado(idPaquete.toString());
            }

            // Timeout / reintentos agotados (edge case): marcar como PENDIENTE_SINCRONIZACION
            if (causa instanceof PendienteSincronizacionException) {
                jsonRecibido = "Fallo de sincronización: " + causa.getMessage();
                registrarLog(idPaquete, AppConstants.HTTP_ERROR_GENERICO, jsonRecibido);
                marcarPendiente(idRuta, idPaquete);
                return SincronizacionResultadoDTO.pendiente(idPaquete.toString());
            }

            // Cualquier otro error HTTP distinto a 200 (SC-002)
            jsonRecibido = "Error inesperado: " + causa.getMessage();
            registrarLog(idPaquete, AppConstants.HTTP_ERROR_GENERICO, jsonRecibido);
            marcarPendiente(idRuta, idPaquete);
            return SincronizacionResultadoDTO.pendiente(idPaquete.toString());

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            registrarLog(idPaquete, AppConstants.HTTP_ERROR_GENERICO, "Hilo interrumpido durante sincronización");
            marcarPendiente(idRuta, idPaquete);
            return SincronizacionResultadoDTO.pendiente(idPaquete.toString());
        }

        // Paso 2: registrar la respuesta HTTP en auditoría (SC-002, FR-003)
        registrarLog(idPaquete, httpStatus, jsonRecibido);

        // Paso 3: mapear el estado recibido a las reglas financieras (FR-002)
        Optional<EstadoPaquete> estadoMapeado = gestionPaqueteMapper.mapearEstado(respuesta);
        if (estadoMapeado.isEmpty()) {
            // Estado no reconocido: omitir cálculo pero conservar el log (edge case)
            return SincronizacionResultadoDTO.estadoNoMapeado(respuesta.estado());
        }

        EstadoPaquete estado = estadoMapeado.get();

        // Paso 4: actualizar estadoActual en Paquete y agregar entrada en HistorialEstado (FR-004, SC-001)
        Paquete paquete = paqueteRepository.findById(idPaquete)
                .map(p -> new Paquete(p.idPaquete(), p.idRuta(), estado))
                .orElse(new Paquete(idPaquete, idRuta, estado));
        paqueteRepository.save(paquete);

        historialRepository.save(new HistorialEstado(null, idPaquete, estado, Instant.now()));

        return SincronizacionResultadoDTO.exitoso(estado.getPorcentajePago(), estado.name());
    }

    @Override
    public Page<HistorialEstadoResponseDTO> obtenerHistorial(UUID idPaquete, Pageable pageable) {
        return historialRepository.findByIdPaquete(idPaquete, pageable)
                .map(h -> new HistorialEstadoResponseDTO(h.id(), h.idPaquete(), h.estado(), h.fecha()));
    }

    @Override
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
                .map(p -> new Paquete(p.idPaquete(), p.idRuta(), EstadoPaquete.PENDIENTE_SINCRONIZACION))
                .orElse(new Paquete(idPaquete, idRuta, EstadoPaquete.PENDIENTE_SINCRONIZACION));
        paqueteRepository.save(paquete);
    }

    private String toJson(GestionPaqueteDTO dto) {
        return "{\"idPaquete\":\"" + dto.idPaquete() + "\",\"estado\":\"" + dto.estado() + "\"}";
    }
}
