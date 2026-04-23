package com.logistica.application.mappers;

import com.logistica.application.dtos.request.RutaCerradaEventDTO;
import com.logistica.domain.models.Parada;
import com.logistica.domain.models.Ruta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RutaEventMapper {

    private final TransportistaEventMapper transportistaMapper;
    private final ParadaEventMapper paradaMapper;

    public Ruta toDomain(RutaCerradaEventDTO dto) {
        if (dto == null) return null;

        List<Parada> paradas = dto.getParadas() == null
                ? List.of()
                : dto.getParadas().stream()
                .map(paradaMapper::toDomain)
                .toList();

        return Ruta.builder()
                .rutaId(dto.getRutaId())
                .transportista(transportistaMapper.toDomain(dto.getConductor()))
                .tipoVehiculo(dto.getVehiculo() != null ? dto.getVehiculo().getTipo() : null)
                .modeloContrato(dto.getConductor() != null ? dto.getConductor().getModeloContrato() : null)
                .fechaInicioTransito(dto.getFechaHoraInicioTransito())
                .fechaCierre(dto.getFechaHoraCierre())
                .paradas(paradas)
                .build();
    }
}