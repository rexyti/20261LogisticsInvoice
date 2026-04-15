package com.logistica.infrastructure.adapters;

import com.logistica.application.dtos.response.LiquidacionResponseDTO;
import com.logistica.domain.models.Liquidacion;
import com.logistica.infrastructure.persistence.entities.LiquidacionEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
        entity.setValorBase(model.getValorBase());
        entity.setValorFinal(model.getValorFinal());
        entity.setFechaCalculo(model.getFechaCalculo());
        entity.setSolicitudRevisionAceptada(model.isSolicitudRevisionAceptada());

        if (model.getAjustes() != null) {
            entity.setAjustes(model.getAjustes().stream()
                    .map(ajuste -> {
                        var e = ajusteMapper.toEntity(ajuste);
                        e.setLiquidacion(entity);
                        return e;
                    }).collect(Collectors.toList()));
        }

        return entity;
    }

    public Liquidacion toModel(LiquidacionEntity entity) {
        if (entity == null) {
            return null;
        }

        return Liquidacion.builder()
                .id(entity.getId())
                .idRuta(entity.getIdRuta())
                .idContrato(entity.getIdContrato())
                .estado(entity.getEstado())
                .valorBase(entity.getValorBase())
                .valorFinal(entity.getValorFinal())
                .fechaCalculo(entity.getFechaCalculo())
                .solicitudRevisionAceptada(entity.isSolicitudRevisionAceptada())
                .ajustes(entity.getAjustes() != null ?
                        entity.getAjustes().stream()
                                .map(ajusteMapper::toModel)
                                .collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }

    public LiquidacionResponseDTO toResponseDTO(Liquidacion model) {
        if (model == null) {
            return null;
        }

        LiquidacionResponseDTO dto = new LiquidacionResponseDTO();
        dto.setId(model.getId());
        dto.setIdRuta(model.getIdRuta());
        dto.setIdContrato(model.getIdContrato());
        dto.setEstado(model.getEstado().name());
        dto.setValorFinal(model.getValorFinal());
        dto.setFechaCalculo(model.getFechaCalculo());
        dto.setSolicitudRevisionAceptada(model.isSolicitudRevisionAceptada());

        if (model.getAjustes() != null) {
            dto.setAjustes(model.getAjustes().stream()
                    .map(ajusteMapper::toResponseDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
