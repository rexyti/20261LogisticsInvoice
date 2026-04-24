package com.logistica.application.mappers;

import com.logistica.application.dtos.response.ParadaResponseDTO;
import com.logistica.domain.models.Parada;
import org.springframework.stereotype.Component;

@Component
public class ParadaResponseMapper {

    public ParadaResponseDTO toResponse(Parada p) {
        if (p == null) return null;

        return ParadaResponseDTO.builder()
                .paradaId(p.getParadaId())
                .estado(p.getEstado() != null ? p.getEstado() : null)
                .motivoFalla(p.getMotivoFalla() != null ? p.getMotivoFalla() : null)
                .responsable(p.getResponsable() != null ? p.getResponsable() : null)
                .build();
    }
}
