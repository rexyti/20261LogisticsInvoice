package com.logistica.application.mappers;

import com.logistica.application.dtos.request.ConductorEventDTO;
import com.logistica.domain.models.Transportista;
import org.springframework.stereotype.Component;

@Component
public class TransportistaEventMapper {

    public Transportista toDomain(ConductorEventDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Conductor es obligatorio en el evento");
        }

        if (dto.getConductorId() == null) {
            throw new IllegalArgumentException("conductorId es obligatorio");
        }

        return Transportista.builder()
                .transportistaId(dto.getConductorId())
                .nombre(dto.getNombre() != null ? dto.getNombre().trim() : null)
                .build();
    }
}