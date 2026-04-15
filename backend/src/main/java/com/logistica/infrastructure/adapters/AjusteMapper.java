package com.logistica.infrastructure.adapters;

import com.logistica.application.dtos.response.AjusteDTO;
import com.logistica.domain.models.Ajuste;
import com.logistica.infrastructure.persistence.entities.AjusteEntity;
import org.springframework.stereotype.Component;

@Component
public class AjusteMapper {

    public AjusteEntity toEntity(Ajuste model) {
        if (model == null) {
            return null;
        }

        AjusteEntity entity = new AjusteEntity();
        entity.setId(model.getId());
        entity.setTipo(model.getTipo());
        entity.setMonto(model.getMonto());
        entity.setMotivo(model.getMotivo());
        return entity;
    }

    public Ajuste toModel(AjusteEntity entity) {
        if (entity == null) {
            return null;
        }

        return Ajuste.builder()
                .id(entity.getId())
                .idLiquidacion(entity.getLiquidacion() != null ? entity.getLiquidacion().getId() : null)
                .tipo(entity.getTipo())
                .monto(entity.getMonto())
                .motivo(entity.getMotivo())
                .build();
    }

    public AjusteDTO toResponseDTO(Ajuste model) {
        if (model == null) {
            return null;
        }

        AjusteDTO dto = new AjusteDTO();
        dto.setId(model.getId());
        dto.setTipo(model.getTipo().name());
        dto.setMonto(model.getMonto());
        dto.setMotivo(model.getMotivo());
        return dto;
    }
}
