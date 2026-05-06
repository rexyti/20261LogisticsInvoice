package com.logistica.infrastructure.liquidacion.persistence.mapper;

import com.logistica.application.liquidacion.dtos.response.AjusteResponseDTO;
import com.logistica.application.liquidacion.dtos.response.LiquidacionResponseDTO;
import com.logistica.domain.liquidacion.models.Liquidacion;
import com.logistica.infrastructure.contratos.persistence.entities.ContratoEntity;
import com.logistica.infrastructure.liquidacion.persistence.entities.LiquidacionEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Mapper {

    private final AjusteMapper ajusteMapper;

    public Mapper(AjusteMapper ajusteMapper) {
        this.ajusteMapper = ajusteMapper;
    }

    public LiquidacionEntity toEntity(Liquidacion model) {
        if (model == null) return null;

        LiquidacionEntity entity = new LiquidacionEntity();
        entity.setId(model.getId());
        entity.setIdRuta(model.getIdRuta());
        
        if (model.getIdContrato() != null) {
            ContratoEntity contrato = new ContratoEntity();
            contrato.setId(model.getIdContrato());
            entity.setContrato(contrato);
        }
        
        entity.setEstado(model.getEstado());
        entity.setValorBase(model.getValorBase());
        entity.setValorFinal(model.getValorFinal());
        entity.setFechaCalculo(model.getFechaCalculo());
        entity.setSolicitudRevisionAceptada(model.isSolicitudRevisionAceptada());
        entity.setIdAdminRevisor(model.getIdAdminRevisor());
        entity.setFechaAceptacionRevision(model.getFechaAceptacionRevision());

        if (model.getAjustes() != null) {
            entity.setAjustes(model.getAjustes().stream()
                    .map(ajuste -> ajusteMapper.toEntity(ajuste, entity))
                    .toList());
        }

        return entity;
    }

    public Liquidacion toModel(LiquidacionEntity entity) {
        if (entity == null) return null;

        return Liquidacion.builder()
                .id(entity.getId())
                .idRuta(entity.getIdRuta())
                .idContrato(entity.getContrato() != null ? entity.getContrato().getId() : null)
                .estado(entity.getEstado())
                .valorBase(entity.getValorBase())
                .valorFinal(entity.getValorFinal())
                .fechaCalculo(entity.getFechaCalculo())
                .solicitudRevisionAceptada(entity.isSolicitudRevisionAceptada())
                .idAdminRevisor(entity.getIdAdminRevisor())
                .fechaAceptacionRevision(entity.getFechaAceptacionRevision())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .ajustes(entity.getAjustes() != null
                        ? entity.getAjustes().stream()
                        .map(ajusteMapper::toModel)
                        .toList()
                        : List.of())
                .build();
    }

    public LiquidacionResponseDTO toResponseDTO(Liquidacion model) {
        if (model == null) return null;

        List<AjusteResponseDTO> ajustesDto = model.getAjustes() != null
                ? model.getAjustes().stream()
                .map(ajusteMapper::toResponseDTO)
                .toList()
                : List.of();

        return LiquidacionResponseDTO.builder()
                .id(model.getId())
                .idRuta(model.getIdRuta())
                .idContrato(model.getIdContrato())
                .estado(model.getEstado())
                .valorBase(model.getValorBase())
                .valorFinal(model.getValorFinal())
                .fechaCalculo(model.getFechaCalculo())
                .idAdminRevisor(model.getIdAdminRevisor())
                .fechaAceptacionRevision(model.getFechaAceptacionRevision())
                .creadoEn(model.getCreatedAt())
                .ajustes(ajustesDto)
                .build();
    }
}
