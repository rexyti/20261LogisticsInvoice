package com.logistica.liquidacion.infrastructure.persistence.mapper;

import com.logistica.liquidacion.domain.models.AuditoriaLiquidacion;
import com.logistica.liquidacion.infrastructure.persistence.entities.AuditoriaLiquidacionEntity;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaLiquidacionMapper {

    /**
     * Crea una nueva entidad de auditoría desde el modelo de dominio.
     */
    public AuditoriaLiquidacionEntity toEntity(AuditoriaLiquidacion model) {
        if (model == null) return null;

        AuditoriaLiquidacionEntity entity = new AuditoriaLiquidacionEntity();
        entity.setIdLiquidacion(model.getIdLiquidacion());
        entity.setOperacion(model.getOperacion());
        entity.setValorAnterior(model.getValorAnterior());
        entity.setValorNuevo(model.getValorNuevo());
        entity.setFechaOperacion(model.getFechaOperacion());
        entity.setTipoResponsable(model.getTipoResponsable());
        entity.setIdResponsable(model.getIdResponsable());
        return entity;
    }

    /**
     * Reconstruye el modelo de dominio desde la entidad persistida.
     */
    public AuditoriaLiquidacion toModel(AuditoriaLiquidacionEntity entity) {
        if (entity == null) return null;

        return AuditoriaLiquidacion.builder()
                .id(entity.getId())
                .idLiquidacion(entity.getIdLiquidacion())
                .operacion(entity.getOperacion())
                .valorAnterior(entity.getValorAnterior())
                .valorNuevo(entity.getValorNuevo())
                .fechaOperacion(entity.getFechaOperacion())
                .tipoResponsable(entity.getTipoResponsable())
                .idResponsable(entity.getIdResponsable())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
