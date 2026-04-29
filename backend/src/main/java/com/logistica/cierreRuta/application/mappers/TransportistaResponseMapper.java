package com.logistica.cierreRuta.application.mappers;

import com.logistica.cierreRuta.application.dtos.response.TransportistaResponseDTO;
import com.logistica.cierreRuta.domain.models.Transportista;
import org.springframework.stereotype.Component;

@Component
public class TransportistaResponseMapper {

    public TransportistaResponseDTO toResponse(Transportista t) {
        if (t == null) return null;

        return TransportistaResponseDTO.builder()
                .TransportistaId(t.getTransportistaId())
                .nombre(t.getNombre())
                .build();
    }
}