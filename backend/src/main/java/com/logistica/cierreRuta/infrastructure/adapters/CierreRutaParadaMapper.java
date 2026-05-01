package com.logistica.cierreRuta.infrastructure.adapters;

import com.logistica.cierreRuta.domain.models.Parada;
import com.logistica.cierreRuta.infrastructure.persistence.entities.CierreRutaParadaEntity;
import org.springframework.stereotype.Component;

@Component
public class CierreRutaParadaMapper {

    public CierreRutaParadaEntity toEntity(Parada parada) {
        if (parada == null) return null;

        return CierreRutaParadaEntity.builder()
                .paradaId(parada.getParadaId())
                .paqueteId(parada.getPaqueteId())
                .estado(parada.getEstado())
                .motivoFalla(parada.getMotivoFalla())
                .build();
    }

    public Parada toDomain(CierreRutaParadaEntity entity) {
        if (entity == null) return null;

        return Parada.builder()
                .paradaId(entity.getParadaId())
                .estado(entity.getEstado())
                .motivoFalla(entity.getMotivoFalla())
                .build();
    }
}
