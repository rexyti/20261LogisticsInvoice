package com.logistica.cierreRuta.infrastructure.adapters;

import com.logistica.cierreRuta.domain.models.CierreRutaTransportista;
import com.logistica.cierreRuta.infrastructure.persistence.entities.CierreRutaTransportistaEntity;
import org.springframework.stereotype.Component;

@Component
public class TransportistaMapper {

    public CierreRutaTransportistaEntity toEntity(CierreRutaTransportista transportista) {
        if (transportista == null) return null;

        if (transportista.getTransportistaId() == null) {
            throw new IllegalArgumentException("El transportista debe tener un ID");
        }


        return CierreRutaTransportistaEntity.builder()
                .conductorId(transportista.getTransportistaId())
                .nombre(transportista.getNombre())
                .build();
    }


    public CierreRutaTransportista toDomain(CierreRutaTransportistaEntity entity) {
        if (entity == null) return null;

        return CierreRutaTransportista.builder()
                .transportistaId(entity.getConductorId())
                .nombre(entity.getNombre())
                .build();
    }
}