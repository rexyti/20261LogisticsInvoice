package com.logistica.infrastructure.adapters;

import com.logistica.domain.models.Parada;
import com.logistica.domain.models.Ruta;
import com.logistica.infrastructure.persistence.entities.ParadaEntity;
import com.logistica.infrastructure.persistence.entities.RutaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RutaMapper {

    private final TransportistaMapper transportistaMapper;
    private final ParadaMapper paradaMapper;

    public RutaEntity toEntity(Ruta ruta) {
        if (ruta == null) return null;

        RutaEntity rutaEntity = RutaEntity.builder()
                .rutaId(ruta.getRutaId())
                .transportista(transportistaMapper.toEntity(ruta.getTransportista()))
                .tipoVehiculo(ruta.getTipoVehiculo())
                .modeloContrato(ruta.getModeloContrato())
                .fechaInicioTransito(ruta.getFechaInicioTransito())
                .fechaCierre(ruta.getFechaCierre())
                .estadoProcesamiento(ruta.getEstadoProcesamiento())
                .build();

        List<ParadaEntity> paradas = (ruta.getParadas() == null ? List.<Parada>of() : ruta.getParadas())
                .stream()
                .map(paradaMapper::toEntity)
                .toList();

        // mantener relación bidireccional
        paradas.forEach(rutaEntity::addParada);

        rutaEntity.setParadas(paradas);

        return rutaEntity;
    }

    public Ruta toDomain(RutaEntity entity) {
        if (entity == null) return null;

        List<Parada> paradas = (entity.getParadas() == null ? List.<ParadaEntity>of() : entity.getParadas())
                .stream()
                .map(paradaMapper::toDomain)
                .toList();

        return Ruta.builder()
                .rutaId(entity.getRutaId())
                .transportista(transportistaMapper.toDomain(entity.getTransportista()))
                .tipoVehiculo(entity.getTipoVehiculo())
                .modeloContrato(entity.getModeloContrato())
                .fechaInicioTransito(entity.getFechaInicioTransito())
                .fechaCierre(entity.getFechaCierre())
                .estadoProcesamiento(entity.getEstadoProcesamiento())
                .paradas(paradas)
                .build();
    }
}