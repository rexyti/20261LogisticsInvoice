package com.logistica.infrastructure.liquidacion.persistence.mapper;

import com.logistica.domain.liquidacion.models.ContratoTarifa;
import com.logistica.infrastructure.liquidacion.persistence.entities.ContratoTarifaEntity;
import org.springframework.stereotype.Component;

@Component
public class LiquidacionContratoMapper {

    public ContratoTarifaEntity toEntity(ContratoTarifa model) {
        if (model == null) return null;

        ContratoTarifaEntity entity = new ContratoTarifaEntity();
        entity.setId(model.getId());
        entity.setTipoContratacion(model.getTipoContratacion());
        entity.setTarifa(model.getTarifa());
        return entity;
    }

    public ContratoTarifa toModel(ContratoTarifaEntity entity) {
        if (entity == null) return null;

        return ContratoTarifa.builder()
                .id(entity.getId())
                .tipoContratacion(entity.getTipoContratacion())
                .tarifa(entity.getTarifa())
                .build();
    }
}
