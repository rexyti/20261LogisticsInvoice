package com.logistica.application.cierreRuta.mappers;

import com.logistica.application.cierreRuta.dtos.response.TransportistaResponseDTO;
import com.logistica.domain.cierreRuta.models.TransportistaRuta;
import org.springframework.stereotype.Component;

@Component
public class TransportistaResponseMapper {

    public TransportistaResponseDTO toResponse(TransportistaRuta t) {
        if (t == null) return null;

        return TransportistaResponseDTO.builder()
                .TransportistaId(t.getTransportistaId())
                .nombre(t.getNombre())
                .build();
    }
}
