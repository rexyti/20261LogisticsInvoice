package com.logistica.application.usecases.paquete;

import com.logistica.application.dtos.response.LogSincronizacionDTO;
import com.logistica.domain.repositories.LogSincronizacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ObtenerLogsSincronizacionUseCase {

    private final LogSincronizacionRepository logSincronizacionRepository;

    public List<LogSincronizacionDTO> findAll() {
        return logSincronizacionRepository.findAll()
                .stream()
                .map(l -> new LogSincronizacionDTO(
                        l.getId(), l.getIdPaquete(),
                        l.getCodigoRespuestaHTTP(), l.getJsonRecibido(), l.getCreatedAt()))
                .toList();
    }

    public List<LogSincronizacionDTO> findByIdPaquete(Long idPaquete) {
        return logSincronizacionRepository.findByIdPaquete(idPaquete)
                .stream()
                .map(l -> new LogSincronizacionDTO(
                        l.getId(), l.getIdPaquete(),
                        l.getCodigoRespuestaHTTP(), l.getJsonRecibido(), l.getCreatedAt()))
                .toList();
    }
}
