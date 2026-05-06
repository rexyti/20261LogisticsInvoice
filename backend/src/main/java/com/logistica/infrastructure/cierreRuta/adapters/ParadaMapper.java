package com.logistica.infrastructure.cierreRuta.adapters;

import com.logistica.domain.cierreRuta.models.Parada;
import com.logistica.infrastructure.cierreRuta.persistence.entities.ParadaEntity;
import org.springframework.stereotype.Component;

@Component
public class ParadaMapper {

    public ParadaEntity toEntity(Parada parada) {
        if (parada == null) return null;

        return ParadaEntity.builder()
                .paradaId(parada.getParadaId())
                .paqueteId(parada.getPaqueteId())
                .estado(parada.getEstado())
                .motivoFalla(parada.getMotivoFalla())
                .build();
    }

    public Parada toDomain(ParadaEntity entity) {
        if (entity == null) return null;

        return Parada.builder()
                .paradaId(entity.getParadaId())
                .estado(entity.getEstado())
                .motivoFalla(entity.getMotivoFalla())
                .build();
    }
}
