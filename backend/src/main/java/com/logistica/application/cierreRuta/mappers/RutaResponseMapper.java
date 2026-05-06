package com.logistica.application.cierreRuta.mappers;

import com.logistica.application.cierreRuta.dtos.response.RutaProcesadaResponseDTO;
import com.logistica.domain.cierreRuta.models.RutaCerrada;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RutaResponseMapper {

    private final TransportistaResponseMapper transportistaMapper;
    private final ParadaResponseMapper paradaMapper;

    public RutaProcesadaResponseDTO toResponse(RutaCerrada ruta) {
        if (ruta == null) return null;

        return RutaProcesadaResponseDTO.builder()
                .rutaId(ruta.getRutaId())
                .vehiculoId(ruta.getVehiculoId())
                .tipoVehiculo(ruta.getTipoVehiculo() != null ? ruta.getTipoVehiculo().name() : null)
                .modeloContrato(ruta.getModeloContrato())
                .estadoProcesamiento(ruta.getEstadoProcesamiento())
                .fechaInicioTransito(ruta.getFechaInicioTransito())
                .fechaCierre(ruta.getFechaCierre())
                .transportista(transportistaMapper.toResponse(ruta.getTransportista()))
                .paradas(
                        ruta.getParadas() != null
                                ? ruta.getParadas().stream().map(paradaMapper::toResponse).toList()
                                : List.of()
                )
                .build();
    }
}
