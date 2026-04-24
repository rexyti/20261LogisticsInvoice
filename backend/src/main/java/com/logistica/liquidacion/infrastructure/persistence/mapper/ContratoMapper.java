package com.logistica.liquidacion.infrastructure.persistence.mapper;

import com.logistica.liquidacion.domain.models.Contrato;
import com.logistica.liquidacion.infrastructure.persistence.entities.ContratoEntity;
import org.springframework.stereotype.Component;

@Component
public class ContratoMapper {

    public ContratoEntity toEntity(Contrato model) {
        if (model == null) return null;

        ContratoEntity entity = new ContratoEntity();
        entity.setId(model.getId());
        entity.setTipoContratacion(model.getTipoContratacion());
        entity.setTarifa(model.getTarifa());
        return entity;
    }

    public Contrato toModel(ContratoEntity entity) {
        if (entity == null) return null;

        return Contrato.builder()
                .id(entity.getId())
                .tipoContratacion(entity.getTipoContratacion())
                .tarifa(entity.getTarifa())
                .build();
    }
}
