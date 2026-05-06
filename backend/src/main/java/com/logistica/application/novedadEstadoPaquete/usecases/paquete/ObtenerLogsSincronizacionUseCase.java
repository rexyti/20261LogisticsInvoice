package com.logistica.application.novedadEstadoPaquete.usecases.paquete;

import com.logistica.application.novedadEstadoPaquete.dtos.response.LogSincronizacionDTO;
import com.logistica.domain.novedadEstadoPaquete.models.LogSincronizacion;
import com.logistica.domain.novedadEstadoPaquete.repositories.LogSincronizacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ObtenerLogsSincronizacionUseCase {

    private final LogSincronizacionRepository logSincronizacionRepository;

    public List<LogSincronizacionDTO> findAll(int page, int size) {
        return logSincronizacionRepository.findAll(page, size)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<LogSincronizacionDTO> findByIdPaquete(Long idPaquete, int page, int size) {
        return logSincronizacionRepository.findByIdPaquete(idPaquete, page, size)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private LogSincronizacionDTO toDto(LogSincronizacion log) {
        return new LogSincronizacionDTO(
                log.getId(),
                log.getIdPaquete(),
                log.getCodigoRespuestaHTTP(),
                log.getJsonRecibido(),
                log.getCreatedAt()
        );
    }
}