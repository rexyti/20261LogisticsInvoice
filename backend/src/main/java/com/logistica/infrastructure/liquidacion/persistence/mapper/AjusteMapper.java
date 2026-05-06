package com.logistica.infrastructure.liquidacion.persistence.mapper;

import com.logistica.application.liquidacion.dtos.request.AjusteDTO;
import com.logistica.application.liquidacion.dtos.response.AjusteResponseDTO;
import com.logistica.domain.liquidacion.models.Ajuste;
import com.logistica.infrastructure.liquidacion.persistence.entities.AjusteEntity;
import com.logistica.infrastructure.liquidacion.persistence.entities.LiquidacionEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AjusteMapper {

    public AjusteEntity toEntity(Ajuste model, LiquidacionEntity liquidacionEntity) {
        if (model == null) return null;

        AjusteEntity entity = new AjusteEntity();
        entity.setId(model.getId());
        entity.setLiquidacion(liquidacionEntity);
        entity.setTipo(model.getTipo());
        entity.setMonto(model.getMonto());
        entity.setMotivo(model.getMotivo());
        return entity;
    }

    public AjusteEntity toEntity(Ajuste model) {
        if (model == null) return null;

        AjusteEntity entity = new AjusteEntity();
        entity.setId(model.getId());

        if (model.getIdLiquidacion() != null) {
            LiquidacionEntity liq = new LiquidacionEntity();
            liq.setId(model.getIdLiquidacion());
            entity.setLiquidacion(liq);
        }

        entity.setTipo(model.getTipo());
        entity.setMonto(model.getMonto());
        entity.setMotivo(model.getMotivo());
        return entity;
    }

    public Ajuste toModel(AjusteEntity entity) {
        if (entity == null) return null;

        return Ajuste.builder()
                .id(entity.getId())
                .idLiquidacion(entity.getLiquidacion() != null
                        ? entity.getLiquidacion().getId()
                        : null)
                .tipo(entity.getTipo())
                .monto(entity.getMonto())
                .motivo(entity.getMotivo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public AjusteResponseDTO toResponseDTO(Ajuste model) {
        if (model == null) return null;

        return AjusteResponseDTO.builder()
                .id(model.getId())
                .idLiquidacion(model.getIdLiquidacion())
                .tipo(model.getTipo())
                .monto(model.getMonto())
                .motivo(model.getMotivo())
                .creadoEn(model.getCreatedAt())
                .build();
    }

    public Ajuste toModel(AjusteDTO dto) {
        if (dto == null) return null;

        return Ajuste.builder()
                .id(dto.getId() != null ? dto.getId() : UUID.randomUUID())
                .idLiquidacion(null)
                .tipo(dto.getTipo())
                .monto(dto.getMonto())
                .motivo(dto.getMotivo())
                .build();
    }

    public List<Ajuste> toModelList(List<AjusteDTO> dtos) {
        if (dtos == null) return List.of();

        return dtos.stream()
                .map(this::toModel)
                .toList();
    }
}
