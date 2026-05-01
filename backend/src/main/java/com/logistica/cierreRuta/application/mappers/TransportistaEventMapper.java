package com.logistica.cierreRuta.application.mappers;

import com.logistica.cierreRuta.application.dtos.request.ConductorEventDTO;
import com.logistica.cierreRuta.domain.models.CierreRutaTransportista;
import org.springframework.stereotype.Component;

@Component
public class TransportistaEventMapper {

    public CierreRutaTransportista toDomain(ConductorEventDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Conductor es obligatorio en el evento");
        }

        if (dto.getConductorId() == null) {
            throw new IllegalArgumentException("conductorId es obligatorio");
        }

        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("nombre del conductor es obligatorio en el evento");
        }

        return CierreRutaTransportista.builder()
                .transportistaId(dto.getConductorId())
                .nombre(dto.getNombre() != null ? dto.getNombre().trim() : null)
                .build();
    }
}