package com.logistica.cierreRuta.infrastructure.adapters;

import com.logistica.cierreRuta.domain.models.Transportista;
import com.logistica.cierreRuta.infrastructure.persistence.entities.TransportistaEntity;
import org.springframework.stereotype.Component;

@Component
public class TransportistaMapper {

    public TransportistaEntity toEntity(Transportista transportista) {
        if (transportista == null) return null;

        if (transportista.getTransportistaId() == null) {
            throw new IllegalArgumentException("El transportista debe tener un ID");
        }


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