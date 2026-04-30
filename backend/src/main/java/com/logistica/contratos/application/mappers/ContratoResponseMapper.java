package com.logistica.contratos.application.mappers;

import com.logistica.contratos.application.dtos.response.ContratoResponseDTO;
import com.logistica.contratos.application.dtos.response.SeguroResponseDTO;
import com.logistica.contratos.application.dtos.response.TransportistaResponseDTO;
import com.logistica.contratos.domain.models.Contrato;
import org.springframework.stereotype.Component;

@Component
public class ContratoResponseMapper {

    public ContratoResponseDTO toResponseDTO(Contrato contrato) {
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

    private TransportistaResponseDTO mapTransportista(Contrato contrato) {
        if (contrato.getTransportista() == null) return null;
        return TransportistaResponseDTO.builder()
                .transportistaId(contrato.getTransportista().getTransportistaId())
                .nombre(contrato.getTransportista().getNombre())
                .build();
    }

    private SeguroResponseDTO mapSeguro(Contrato contrato) {
        if (contrato.getSeguro() == null) return null;
        return SeguroResponseDTO.builder()
                .id(contrato.getSeguro().getIdSeguro())
                .numeroPoliza(contrato.getSeguro().getNumeroPoliza())
                .estado(contrato.getSeguro().getEstado())
                .build();
    }
}