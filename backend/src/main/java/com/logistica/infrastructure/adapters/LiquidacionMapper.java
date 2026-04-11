package com.logistica.infrastructure.adapters;

import com.logistica.domain.models.Liquidacion;
import com.logistica.infrastructure.persistence.entities.LiquidacionEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class LiquidacionMapper {

    private final AjusteMapper ajusteMapper;

    public LiquidacionMapper(AjusteMapper ajusteMapper) {
        this.ajusteMapper = ajusteMapper;
    }

    public LiquidacionEntity toEntity(Liquidacion model) {
        if (model == null) {
            return null;
        }

        LiquidacionEntity entity = new LiquidacionEntity();
        entity.setId(model.getId());
        entity.setIdRuta(model.getIdRuta());
        entity.setIdContrato(model.getIdContrato());
        entity.setEstado(model.getEstado());
        entity.setValorFinal(model.getValorFinal());
        entity.setFechaCalculo(model.getFechaCalculo());
        if (model.getAjustes() != null) {
            entity.setAjustes(model.getAjustes().stream()
                    .map(ajusteMapper::toEntity)
                    .collect(Collectors.toList()));
            entity.getAjustes().forEach(ajusteEntity -> ajusteEntity.setLiquidacion(entity));
        }
        return entity;
    }

    public Liquidacion toModel(LiquidacionEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Liquidacion(
                entity.getId(),
                entity.getIdRuta(),
                entity.getIdContrato(),
                entity.getEstado(),
                entity.getValorFinal(),
                entity.getFechaCalculo(),
                entity.getAjustes() != null ? entity.getAjustes().stream()
                        .map(ajusteMapper::toModel)
                        .collect(Collectors.toList()) : null
        );
    }
}
