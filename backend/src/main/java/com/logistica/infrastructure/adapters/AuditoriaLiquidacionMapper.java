package com.logistica.infrastructure.adapters;

import com.logistica.domain.models.AuditoriaLiquidacion;
import com.logistica.infrastructure.persistence.entities.AuditoriaLiquidacionEntity;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaLiquidacionMapper {

    public AuditoriaLiquidacionEntity toEntity(AuditoriaLiquidacion model) {
        if (model == null) {
            return null;
        }

        AuditoriaLiquidacionEntity entity = new AuditoriaLiquidacionEntity();
        entity.setId(model.getId());
        entity.setIdLiquidacion(model.getIdLiquidacion());
        entity.setOperacion(model.getOperacion());
        entity.setValorAnterior(model.getValorAnterior());
        entity.setValorNuevo(model.getValorNuevo());
        entity.setFechaOperacion(model.getFechaOperacion());
        entity.setResponsable(model.getResponsable());
        return entity;
    }

    public AuditoriaLiquidacion toModel(AuditoriaLiquidacionEntity entity) {
        if (entity == null) {
            return null;
        }

        return new AuditoriaLiquidacion(
                entity.getId(),
                entity.getIdLiquidacion(),
                entity.getOperacion(),
                entity.getValorAnterior(),
                entity.getValorNuevo(),
                entity.getFechaOperacion(),
                entity.getResponsable()
        );
    }
}
