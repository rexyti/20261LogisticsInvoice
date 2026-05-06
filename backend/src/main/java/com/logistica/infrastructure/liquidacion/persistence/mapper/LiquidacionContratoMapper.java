package com.logistica.infrastructure.liquidacion.persistence.mapper;

import com.logistica.domain.liquidacion.enums.TipoContratacion;
import com.logistica.domain.liquidacion.models.ContratoTarifa;
import com.logistica.infrastructure.contratos.persistence.entities.ContratoEntity;
import org.springframework.stereotype.Component;

@Component
public class LiquidacionContratoMapper {

    public ContratoEntity toEntity(ContratoTarifa model) {
        if (model == null) return null;

        ContratoEntity entity = new ContratoEntity();
        entity.setId(model.getId());
        entity.setTipoContratacion(model.getTipoContratacion() != null ? model.getTipoContratacion().name() : null);
        entity.setTarifa(model.getTarifa());
        return entity;
    }

    public ContratoTarifa toModel(ContratoEntity entity) {
        if (entity == null) return null;

        return ContratoTarifa.builder()
                .id(entity.getId())
                .tipoContratacion(entity.getTipoContratacion() != null
                        ? TipoContratacion.valueOf(entity.getTipoContratacion()) : null)
                .tarifa(entity.getTarifa())
                .build();
    }
}
