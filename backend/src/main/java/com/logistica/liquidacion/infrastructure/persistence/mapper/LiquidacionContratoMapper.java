package com.logistica.liquidacion.infrastructure.persistence.mapper;

import com.logistica.liquidacion.domain.models.LiquidacionContrato;
import com.logistica.liquidacion.infrastructure.persistence.entities.LiquidacionContratoEntity;
import org.springframework.stereotype.Component;

@Component
public class LiquidacionContratoMapper {

    public LiquidacionContratoEntity toEntity(LiquidacionContrato model) {
        if (model == null) return null;

        LiquidacionContratoEntity entity = new LiquidacionContratoEntity();
        entity.setId(model.getId());
        entity.setTipoContratacion(model.getTipoContratacion());
        entity.setTarifa(model.getTarifa());
        return entity;
    }

    public LiquidacionContrato toModel(LiquidacionContratoEntity entity) {
        if (entity == null) return null;

        return LiquidacionContrato.builder()
                .id(entity.getId())
                .tipoContratacion(entity.getTipoContratacion())
                .tarifa(entity.getTarifa())
                .build();
    }
}
