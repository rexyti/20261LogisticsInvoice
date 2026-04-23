package com.logistica.infrastructure.adapters;

import com.logistica.application.dtos.request.ContratoRequestDTO;
import com.logistica.application.dtos.response.ContratoResponseDTO;
import com.logistica.domain.models.Contrato;
import com.logistica.infrastructure.persistence.entities.ContratoEntity;
import com.logistica.infrastructure.persistence.entities.UsuarioEntity;
import com.logistica.infrastructure.persistence.entities.VehiculoEntity;
import org.springframework.stereotype.Component;

@Component
public class ContratoMapper {

    public Contrato toDomain(ContratoRequestDTO dto, Long idUsuario, Long idVehiculo) {
        return Contrato.builder()
                .idContrato(dto.getIdContrato())
                .tipoContrato(dto.getTipoContrato())
                .nombreConductor(dto.getNombreConductor())
                .precioParadas(dto.getPrecioParadas())
                .precio(dto.getPrecio())
                .tipoVehiculo(dto.getTipoVehiculo())
                .fechaInicio(dto.getFechaInicio())
                .fechaFinal(dto.getFechaFinal())
                .estadoSeguro(dto.getEstadoSeguro())
                .idUsuario(idUsuario)
                .idVehiculo(idVehiculo)
                .build();
    }

    public ContratoEntity toEntity(Contrato contrato, UsuarioEntity usuario, VehiculoEntity vehiculo) {
        return ContratoEntity.builder()
                .idContrato(contrato.getIdContrato())
                .tipoContrato(contrato.getTipoContrato())
                .nombreConductor(contrato.getNombreConductor())
                .precioParadas(contrato.getPrecioParadas())
                .precio(contrato.getPrecio())
                .tipoVehiculo(contrato.getTipoVehiculo())
                .fechaInicio(contrato.getFechaInicio())
                .fechaFinal(contrato.getFechaFinal())
                .usuario(usuario)
                .vehiculo(vehiculo)
                .build();
    }

    public Contrato toDomainFromEntity(ContratoEntity entity) {
        String estadoSeguro = entity.getUsuario() != null
                && entity.getUsuario().getSeguros() != null
                && !entity.getUsuario().getSeguros().isEmpty()
                ? entity.getUsuario().getSeguros().get(0).getEstado()
                : null;

        return Contrato.builder()
                .id(entity.getId())
                .idContrato(entity.getIdContrato())
                .tipoContrato(entity.getTipoContrato())
                .nombreConductor(entity.getNombreConductor())
                .precioParadas(entity.getPrecioParadas())
                .precio(entity.getPrecio())
                .tipoVehiculo(entity.getTipoVehiculo())
                .fechaInicio(entity.getFechaInicio())
                .fechaFinal(entity.getFechaFinal())
                .estadoSeguro(estadoSeguro)
                .idUsuario(entity.getUsuario() != null ? entity.getUsuario().getIdUsuario() : null)
                .idVehiculo(entity.getVehiculo() != null ? entity.getVehiculo().getIdVehiculo() : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public ContratoResponseDTO toResponseDTO(Contrato contrato) {
        return ContratoResponseDTO.builder()
                .id(contrato.getId())
                .idContrato(contrato.getIdContrato())
                .tipoContrato(contrato.getTipoContrato())
                .nombreConductor(contrato.getNombreConductor())
                .precioParadas(contrato.getPrecioParadas())
                .precio(contrato.getPrecio())
                .tipoVehiculo(contrato.getTipoVehiculo())
                .fechaInicio(contrato.getFechaInicio())
                .fechaFinal(contrato.getFechaFinal())
                .estadoSeguro(contrato.getEstadoSeguro())
                .createdAt(contrato.getCreatedAt())
                .build();
    }
}
