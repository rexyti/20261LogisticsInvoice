package com.logistica.application.mappers;

import com.logistica.application.dtos.request.RutaCerradaEventDTO;
import com.logistica.domain.models.Parada;
import com.logistica.domain.models.Ruta;
import com.logistica.domain.models.Transportista;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RutaEventMapper {

    private final TransportistaEventMapper transportistaMapper;
    private final ParadaEventMapper paradaMapper;

    public Ruta toDomain(RutaCerradaEventDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Evento RutaCerradaEventDTO no puede ser null");
        }

        if (dto.getRutaId() == null) {
            throw new IllegalArgumentException("rutaId es obligatorio");
        }

        List<Parada> paradas = mapParadas(dto);

        return Ruta.builder()
                .rutaId(dto.getRutaId())
                .transportista(mapTransportista(dto))
                .tipoVehiculo(mapTipoVehiculo(dto))
                .modeloContrato(mapModeloContrato(dto))
                .fechaInicioTransito(dto.getFechaHoraInicioTransito())
                .fechaCierre(dto.getFechaHoraCierre())
                .paradas(paradas)
                .build();
    }

    private List<Parada> mapParadas(RutaCerradaEventDTO dto) {
        if (dto.getParadas() == null || dto.getParadas().isEmpty()) {
            return List.of();
        }

        return dto.getParadas().stream()
                .filter(Objects::nonNull)
                .map(paradaMapper::toDomain)
                .toList();
    }

    private Transportista mapTransportista(RutaCerradaEventDTO dto) {
        if (dto.getConductor() == null) return null;
        return transportistaMapper.toDomain(dto.getConductor());
    }

    private String mapTipoVehiculo(RutaCerradaEventDTO dto) {
        if (dto.getVehiculo() == null) return null;
        return dto.getVehiculo().getTipo();
    }

    private String mapModeloContrato(RutaCerradaEventDTO dto) {
        if (dto.getConductor() == null) return null;
        return dto.getConductor().getModeloContrato();
    }
}
