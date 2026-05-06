package com.logistica.application.cierreRuta.mappers;

import com.logistica.application.cierreRuta.dtos.request.ConductorEventDTO;
import com.logistica.domain.cierreRuta.models.TransportistaRuta;
import org.springframework.stereotype.Component;

@Component
public class TransportistaEventMapper {

    public TransportistaRuta toDomain(ConductorEventDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Conductor es obligatorio en el evento");
        }

        if (dto.getConductorId() == null) {
            throw new IllegalArgumentException("conductorId es obligatorio");
        }

        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("nombre del conductor es obligatorio en el evento");
        }

        return TransportistaRuta.builder()
                .transportistaId(dto.getConductorId())
                .nombre(dto.getNombre() != null ? dto.getNombre().trim() : null)
                .build();
    }
}
