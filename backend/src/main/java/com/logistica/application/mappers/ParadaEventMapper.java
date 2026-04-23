package com.logistica.application.mappers;

import com.logistica.application.dtos.request.ParadaEventDTO;
import com.logistica.domain.enums.MotivoFalla;
import com.logistica.domain.models.Parada;
import org.springframework.stereotype.Component;

@Component
public class ParadaEventMapper {

    public Parada toDomain(ParadaEventDTO dto) {
        if (dto == null) return null;

        MotivoFalla motivo = null;

        if (dto.getMotivoNoEntrega() != null) {
            motivo = MotivoFalla.fromValue(dto.getMotivoNoEntrega());
        }

        return Parada.builder()
                .paradaId(dto.getParadaId())
                .estado(dto.getEstado())
                .motivoFalla(motivo)
                // responsable se calcula después en dominio/service
                .build();
    }
}