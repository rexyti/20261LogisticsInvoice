package com.logistica.cierreRuta.application.mappers;

import com.logistica.cierreRuta.application.dtos.response.CierreRutaTransportistaResponseDTO;
import com.logistica.cierreRuta.domain.models.CierreRutaTransportista;
import org.springframework.stereotype.Component;

@Component
public class TransportistaResponseMapper {

    public CierreRutaTransportistaResponseDTO toResponse(CierreRutaTransportista t) {
        if (t == null) return null;

        return CierreRutaTransportistaResponseDTO.builder()
                .TransportistaId(t.getTransportistaId())
                .nombre(t.getNombre())
                .build();
    }
}