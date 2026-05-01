package com.logistica.cierreRuta.application.mappers;

import com.logistica.cierreRuta.application.dtos.response.CierreRutaRutaProcesadaResponseDTO;
import com.logistica.cierreRuta.domain.models.CierreRutaRuta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CierreRutaRutaResponseMapper {

    private final CierreRutaTransportistaResponseMapper transportistaMapper;
    private final CierreRutaParadaResponseMapper paradaMapper;

    public CierreRutaRutaProcesadaResponseDTO toResponse(CierreRutaRuta ruta) {
        if (ruta == null) return null;

        return CierreRutaRutaProcesadaResponseDTO.builder()
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
