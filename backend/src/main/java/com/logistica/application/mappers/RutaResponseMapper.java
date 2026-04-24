package com.logistica.application.mappers;

import com.logistica.application.dtos.response.RutaProcesadaResponseDTO;
import com.logistica.domain.models.Ruta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RutaResponseMapper {

    private final TransportistaResponseMapper transportistaMapper;
    private final ParadaResponseMapper paradaMapper;

    public RutaProcesadaResponseDTO toResponse(Ruta ruta) {
        if (ruta == null) return null;

        return RutaProcesadaResponseDTO.builder()
                .rutaId(ruta.getRutaId())
                .vehiculoId(ruta.getVehiculoId())
                .tipoVehiculo(ruta.getTipoVehiculo())
                .modeloContrato(ruta.getModeloContrato())
                .estadoProcesamiento(ruta.getEstadoProcesamiento())
                .fechaInicioTransito(ruta.getFechaInicioTransito())
                .fechaCierre(ruta.getFechaCierre())
                .transportista(transportistaMapper.toResponse(ruta.getTransportista()))
                .paradas(
                        Optional.ofNullable(ruta.getParadas())
                                .orElse(List.of())
                                .stream()
                                .map(paradaMapper::toResponse)
                                .toList()
                )
                .build();
    }
}
