package com.logistica.application.cierreRuta.mappers;

import com.logistica.application.cierreRuta.dtos.request.RutaCerradaEventDTO;
import com.logistica.domain.shared.enums.TipoVehiculo;
import com.logistica.domain.cierreRuta.models.Parada;
import com.logistica.domain.cierreRuta.models.RutaCerrada;
import com.logistica.domain.cierreRuta.models.TransportistaRuta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RutaEventMapper {

    private final TransportistaEventMapper transportistaMapper;
    private final ParadaEventMapper paradaMapper;

    public RutaCerrada toDomain(RutaCerradaEventDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Evento RutaCerradaEventDTO no puede ser null");
        }

        if (dto.getRutaId() == null) {
            throw new IllegalArgumentException("rutaId es obligatorio");
        }

        List<Parada> paradas = mapParadas(dto);

        return RutaCerrada.builder()
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

    private List<Parada> mapParadas(RutaCerradaEventDTO dto) {
        if (dto.getParadas() == null || dto.getParadas().isEmpty()) {
            return Collections.emptyList();
        }

        return dto.getParadas().stream()
                .filter(Objects::nonNull)
                .map(paradaMapper::toDomain)
                .toList();
    }

    private TransportistaRuta mapTransportista(RutaCerradaEventDTO dto) {
        if (dto.getConductor() == null) {
            throw new IllegalArgumentException("El conductor es obligatorio");
        }
        return transportistaMapper.toDomain(dto.getConductor());
    }

    private UUID mapVehiculoId(RutaCerradaEventDTO dto) {
        if (dto.getVehiculo() == null) return null;
        return dto.getVehiculo().getVehiculoId();
    }

    private TipoVehiculo mapTipoVehiculo(RutaCerradaEventDTO dto) {
        if (dto.getVehiculo() == null || dto.getVehiculo().getTipo() == null) {
            return null;
        }

        return TipoVehiculo.from(dto.getVehiculo().getTipo());
    }

    private String mapModeloContrato(RutaCerradaEventDTO dto) {
        if (dto.getConductor() == null) return null;
        return dto.getConductor().getModeloContrato();
    }
}
