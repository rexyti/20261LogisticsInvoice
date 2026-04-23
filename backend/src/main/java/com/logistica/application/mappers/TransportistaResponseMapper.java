package com.logistica.application.mappers;

import com.logistica.application.dtos.response.TransportistaResponseDTO;
import com.logistica.domain.models.Transportista;
import org.springframework.stereotype.Component;

@Component
public class TransportistaResponseMapper {

    public TransportistaResponseDTO toResponse(Transportista t) {
        if (t == null) return null;

        return TransportistaResponseDTO.builder()
                .conductorId(t.getTransportistaId())
                .nombre(t.getNombre())
                .build();
    }
}