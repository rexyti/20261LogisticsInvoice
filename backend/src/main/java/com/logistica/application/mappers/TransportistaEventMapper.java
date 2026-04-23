package com.logistica.application.mappers;

import com.logistica.application.dtos.request.ConductorEventDTO;
import com.logistica.domain.models.Transportista;
import org.springframework.stereotype.Component;

@Component
public class TransportistaEventMapper {

    public Transportista toDomain(ConductorEventDTO dto) {
        if (dto == null) return null;

        return Transportista.builder()
                .transportistaId(dto.getConductorId())
                .nombre(dto.getNombre() != null ? dto.getNombre().trim() : null)
                .build();
    }
}