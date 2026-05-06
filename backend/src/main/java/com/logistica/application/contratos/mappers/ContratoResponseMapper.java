package com.logistica.application.contratos.mappers;

import com.logistica.application.contratos.dtos.response.ContratoResponseDTO;
import com.logistica.application.contratos.dtos.response.SeguroResponseDTO;
import com.logistica.application.contratos.dtos.response.TransportistaResponseDTO;
import com.logistica.domain.contratos.models.Contrato;
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