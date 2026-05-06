package com.logistica.infrastructure.cierreRuta.adapters;

import com.logistica.domain.cierreRuta.models.TransportistaRuta;
import com.logistica.infrastructure.cierreRuta.persistence.entities.TransportistaEntity;
import org.springframework.stereotype.Component;

@Component
public class TransportistaMapper {

    public TransportistaEntity toEntity(TransportistaRuta transportista) {
        if (transportista == null) return null;

        if (transportista.getTransportistaId() == null) {
            throw new IllegalArgumentException("El transportista debe tener un ID");
        }

        return TransportistaEntity.builder()
                .conductorId(transportista.getTransportistaId())
                .nombre(transportista.getNombre())
                .build();
    }

    public TransportistaRuta toDomain(TransportistaEntity entity) {
        if (entity == null) return null;

        return TransportistaRuta.builder()
                .transportistaId(entity.getConductorId())
                .nombre(entity.getNombre())
                .build();
    }
}
