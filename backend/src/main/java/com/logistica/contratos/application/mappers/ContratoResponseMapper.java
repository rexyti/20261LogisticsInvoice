package com.logistica.contratos.application.mappers;

import com.logistica.contratos.application.dtos.response.ContratoResponseDTO;
import com.logistica.contratos.application.dtos.response.SeguroResponseDTO;
import com.logistica.contratos.application.dtos.response.ContratosTransportistaResponseDTO;
import com.logistica.contratos.domain.models.ContratosContrato;
import org.springframework.stereotype.Component;

@Component
public class ContratoResponseMapper {

    public ContratoResponseDTO toResponseDTO(ContratosContrato contrato) {
        return ContratoResponseDTO.builder()
                .id(contrato.getId())
                .idContrato(contrato.getIdContrato())
                .tipoContrato(contrato.getTipoContrato())
                .transportista(mapTransportista(contrato))
                .tipoVehiculo(contrato.getTipoVehiculo())
                .esPorParada(contrato.getEsPorParada())
                .precioParadas(contrato.getPrecioParadas())
                .precio(contrato.getPrecio())
                .fechaInicio(contrato.getFechaInicio())
                .fechaFinal(contrato.getFechaFinal())
                .seguro(mapSeguro(contrato))
                .build();
    }

    private ContratosTransportistaResponseDTO mapTransportista(ContratosContrato contrato) {
        if (contrato.getTransportista() == null) return null;
        return ContratosTransportistaResponseDTO.builder()
                .transportistaId(contrato.getTransportista().getTransportistaId())
                .nombre(contrato.getTransportista().getNombre())
                .build();
    }

    private SeguroResponseDTO mapSeguro(ContratosContrato contrato) {
        if (contrato.getSeguro() == null) return null;
        return SeguroResponseDTO.builder()
                .id(contrato.getSeguro().getIdSeguro())
                .numeroPoliza(contrato.getSeguro().getNumeroPoliza())
                .estado(contrato.getSeguro().getEstado())
                .build();
    }
}