package com.logistica.cierreRuta.application.mappers;

import com.logistica.cierreRuta.application.dtos.request.CierreRutaRutaCerradaEventDTO;
import com.logistica.cierreRuta.domain.enums.CierreRutaTipoVehiculo;
import com.logistica.cierreRuta.domain.models.Parada;
import com.logistica.cierreRuta.domain.models.CierreRutaRuta;
import com.logistica.cierreRuta.domain.models.CierreRutaTransportista;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CierreRutaRutaEventMapper {

    private final CierreRutaTransportistaEventMapper transportistaMapper;
    private final CierreRutaParadaEventMapper paradaMapper;

    public CierreRutaRuta toDomain(CierreRutaRutaCerradaEventDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Evento CierreRutaRutaCerradaEventDTO no puede ser null");
        }

        if (dto.getRutaId() == null) {
            throw new IllegalArgumentException("rutaId es obligatorio");
        }

        List<Parada> paradas = mapParadas(dto);

        return CierreRutaRuta.builder()
                .rutaId(dto.getRutaId())
                .transportista(mapTransportista(dto))
                .vehiculoId(mapVehiculoId(dto))
                .tipoVehiculo(mapTipoVehiculo(dto))
                .modeloContrato(mapModeloContrato(dto))
                .fechaInicioTransito(dto.getFechaHoraInicioTransito())
                .fechaCierre(dto.getFechaHoraCierre())
                .paradas(paradas)
                .build();
    }

    private List<Parada> mapParadas(CierreRutaRutaCerradaEventDTO dto) {
        if (dto.getParadas() == null || dto.getParadas().isEmpty()) {
            return Collections.emptyList();
        }

        return dto.getParadas().stream()
                .filter(Objects::nonNull)
                .map(paradaMapper::toDomain)
                .toList();
    }

    private CierreRutaTransportista mapTransportista(CierreRutaRutaCerradaEventDTO dto) {
        if (dto.getConductor() == null) {
            throw new IllegalArgumentException("El conductor es obligatorio");
        }
        return transportistaMapper.toDomain(dto.getConductor());
    }

    private UUID mapVehiculoId(CierreRutaRutaCerradaEventDTO dto) {
        if (dto.getVehiculo() == null) return null;
        return dto.getVehiculo().getVehiculoId();
    }

    private CierreRutaTipoVehiculo mapTipoVehiculo(CierreRutaRutaCerradaEventDTO dto) {
        if (dto.getVehiculo() == null || dto.getVehiculo().getTipo() == null) {
            return null;
        }

        return CierreRutaTipoVehiculo.from(dto.getVehiculo().getTipo());
    }

    private String mapModeloContrato(CierreRutaRutaCerradaEventDTO dto) {
        if (dto.getConductor() == null) return null;
        return dto.getConductor().getModeloContrato();
    }
}
