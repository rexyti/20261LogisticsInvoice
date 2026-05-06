package com.logistica.infrastructure.liquidacion.persistence.mapper;

import com.logistica.domain.liquidacion.models.AuditoriaLiquidacion;
import com.logistica.infrastructure.liquidacion.persistence.entities.AuditoriaEntity;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaMapper {

    /**
     * Crea una nueva entidad de auditoría desde el modelo de dominio.
     */
    public AuditoriaEntity toEntity(AuditoriaLiquidacion model) {
        if (model == null) return null;

        AuditoriaEntity entity = new AuditoriaEntity();
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
    public AuditoriaLiquidacion toModel(AuditoriaEntity entity) {
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
