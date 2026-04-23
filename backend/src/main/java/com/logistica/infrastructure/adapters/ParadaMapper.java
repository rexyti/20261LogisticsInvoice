package com.logistica.infrastructure.adapters;

import com.logistica.domain.models.Parada;
import com.logistica.infrastructure.persistence.entities.ParadaEntity;
import org.springframework.stereotype.Component;

@Component
public class ParadaMapper {

    public ParadaEntity toEntity(Parada parada) {
        if (parada == null) return null;

        return ParadaEntity.builder()
                .paradaId(parada.getParadaId())
                .estado(parada.getEstado())
                .motivoFalla(parada.getMotivoFalla())
                .responsable(parada.getResponsable())
                .build();
    }

    public Parada toDomain(ParadaEntity entity) {
        if (entity == null) return null;

        return Parada.builder()
                .paradaId(entity.getParadaId())
                .estado(entity.getEstado())
                .motivoFalla(entity.getMotivoFalla())
                .responsableFalla(entity.getResponsable())
                .build();
    }
}