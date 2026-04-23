package com.logistica.infrastructure.adapters;

import com.logistica.domain.models.Transportista;
import com.logistica.infrastructure.persistence.entities.TransportistaEntity;
import org.springframework.stereotype.Component;

@Component
public class TransportistaMapper {

    public TransportistaEntity toEntity(Transportista transportista) {
        if (transportista == null) return null;

        return TransportistaEntity.builder()
                .conductorId(transportista.getTransportistaId())
                .nombre(transportista.getNombre())
                .build();
    }

    public Transportista toDomain(TransportistaEntity entity) {
        if (entity == null) return null;

        return Transportista.builder()
                .transportistaId(entity.getConductorId())
                .nombre(entity.getNombre())
                .build();
    }
}