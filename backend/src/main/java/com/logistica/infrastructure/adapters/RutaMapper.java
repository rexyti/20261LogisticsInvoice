package com.logistica.infrastructure.adapters;

import com.logistica.domain.models.Parada;
import com.logistica.domain.models.Ruta;
import com.logistica.domain.models.Transportista;
import com.logistica.infrastructure.persistence.entities.ParadaEntity;
import com.logistica.infrastructure.persistence.entities.RutaEntity;
import com.logistica.infrastructure.persistence.entities.TransportistaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RutaMapper {

    public RutaEntity toEntity(Ruta ruta) {
        TransportistaEntity transportistaEntity = TransportistaEntity.builder()
                .conductorId(ruta.getTransportista().getConductorId())
                .nombre(ruta.getTransportista().getNombre())
                .build();

        RutaEntity rutaEntity = RutaEntity.builder()
                .rutaId(ruta.getRutaId())
                .transportista(transportistaEntity)
                .tipoVehiculo(ruta.getTipoVehiculo())
                .modeloContrato(ruta.getModeloContrato())
                .fechaInicioTransito(ruta.getFechaInicioTransito())
                .fechaCierre(ruta.getFechaCierre())
                .estadoProcesamiento(ruta.getEstadoProcesamiento())
                .build();

        List<ParadaEntity> paradaEntities = ruta.getParadas().stream()
                .map(p -> ParadaEntity.builder()
                        .paradaId(p.getParadaId())
                        .ruta(rutaEntity)
                        .estado(p.getEstado())
                        .motivoFalla(p.getMotivoFalla())
                        .responsable(p.getResponsable())
                        .build())
                .toList();

        rutaEntity.setParadas(paradaEntities);
        return rutaEntity;
    }

    public Ruta toDomain(RutaEntity entity) {
        Transportista transportista = Transportista.builder()
                .conductorId(entity.getTransportista().getConductorId())
                .nombre(entity.getTransportista().getNombre())
                .build();

        List<Parada> paradas = entity.getParadas().stream()
                .map(p -> Parada.builder()
                        .paradaId(p.getParadaId())
                        .estado(p.getEstado())
                        .motivoFalla(p.getMotivoFalla())
                        .responsable(p.getResponsable())
                        .build())
                .toList();

        return Ruta.builder()
                .rutaId(entity.getRutaId())
                .transportista(transportista)
                .tipoVehiculo(entity.getTipoVehiculo())
                .modeloContrato(entity.getModeloContrato())
                .fechaInicioTransito(entity.getFechaInicioTransito())
                .fechaCierre(entity.getFechaCierre())
                .estadoProcesamiento(entity.getEstadoProcesamiento())
                .paradas(paradas)
                .build();
    }
}
