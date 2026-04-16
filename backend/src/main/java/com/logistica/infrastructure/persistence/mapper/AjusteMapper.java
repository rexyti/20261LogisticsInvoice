package com.logistica.infrastructure.persistence.mapper;

import com.logistica.application.dtos.response.AjusteResponseDTO;
import com.logistica.domain.models.Ajuste;
import com.logistica.infrastructure.persistence.entities.AjusteEntity;
import com.logistica.infrastructure.persistence.entities.LiquidacionEntity;
import org.springframework.stereotype.Component;

@Component
public class AjusteMapper {

    /**
     * Convierte un modelo de dominio a entidad de persistencia.
     * Requiere la LiquidacionEntity ya cargada para establecer la FK correctamente.
     */
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

    /**
     * Versión simplificada para guardar ajustes de forma independiente si es necesario.
     * Crea un proxy de LiquidacionEntity solo con el ID para evitar cargar toda la liquidación.
     */
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

    /**
     * Convierte una entidad de persistencia a modelo de dominio.
     */
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
}
