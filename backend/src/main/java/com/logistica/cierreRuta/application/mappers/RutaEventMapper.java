package com.logistica.cierreRuta.application.mappers;

import com.logistica.cierreRuta.application.dtos.request.RutaCerradaEventDTO;
import com.logistica.cierreRuta.domain.enums.TipoVehiculo;
import com.logistica.cierreRuta.domain.models.Parada;
import com.logistica.cierreRuta.domain.models.CierreRutaRuta;
import com.logistica.cierreRuta.domain.models.Transportista;
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

    public CierreRutaRuta toDomain(RutaCerradaEventDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Evento RutaCerradaEventDTO no puede ser null");
        }

        if (dto.getRutaId() == null) {
            throw new IllegalArgumentException("rutaId es obligatorio");
        }

        List<Parada> paradas = mapParadas(dto);

        return CierreRutaRuta.builder()
                .rutaId(dto.getRutaId())
                .transportista(mapTransportista(dto))
                .vehiculoId(mapVehiculoId(dto))
                .tipoVehiculo(mapTipoVehiculo(dto)) // 🔥 ahora es ENUM
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

    private Transportista mapTransportista(RutaCerradaEventDTO dto) {
        if (dto.getConductor() == null){
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