package com.logistica.application.usecases.paquete;

import com.logistica.application.dtos.response.HistorialEstadoDTO;
import com.logistica.domain.repositories.HistorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ObtenerHistorialUseCase {

    private final HistorialRepository historialRepository;

    public List<HistorialEstadoDTO> execute(Long idPaquete) {
        return historialRepository.findByIdPaqueteOrderByFechaDesc(idPaquete)
                .stream()
                .map(h -> new HistorialEstadoDTO(h.getId(), h.getIdPaquete(), h.getEstado(), h.getFecha()))
                .toList();
    }
}
