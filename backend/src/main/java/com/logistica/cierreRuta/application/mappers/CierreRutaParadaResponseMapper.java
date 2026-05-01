package com.logistica.cierreRuta.application.mappers;

import com.logistica.cierreRuta.application.dtos.response.CierreRutaParadaResponseDTO;
import com.logistica.cierreRuta.domain.models.Parada;
import org.springframework.stereotype.Component;

@Component
public class CierreRutaParadaResponseMapper {

    public CierreRutaParadaResponseDTO toResponse(Parada p) {
        if (p == null) return null;

        return CierreRutaParadaResponseDTO.builder()
                .paradaId(p.getParadaId())
                .estado(p.getEstado() != null ? p.getEstado() : null)
                .motivoFalla(p.getMotivoFalla() != null ? p.getMotivoFalla() : null)
                .responsable(p.getResponsable() != null ? p.getResponsable() : null)
                .build();
    }
}
