package com.logistica.infrastructure.cierreRuta.adapters;

import com.logistica.domain.shared.enums.TipoVehiculo;
import com.logistica.domain.cierreRuta.models.Parada;
import com.logistica.domain.cierreRuta.models.RutaCerrada;
import com.logistica.infrastructure.cierreRuta.persistence.entities.ParadaEntity;
import com.logistica.infrastructure.cierreRuta.persistence.entities.RutaEntity;
import com.logistica.infrastructure.cierreRuta.persistence.entities.TransportistaEntity;
import com.logistica.infrastructure.cierreRuta.persistence.repositories.CierreRutaTransportistaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RutaMapper {

    private final TransportistaMapper transportistaMapper;
    private final CierreRutaTransportistaJpaRepository transportistaJpaRepository;
    private final ParadaMapper paradaMapper;

    public RutaEntity toEntity(RutaCerrada ruta) {
        if (ruta == null) return null;

        if (ruta.getTransportista() == null || ruta.getTransportista().getTransportistaId() == null) {
            throw new IllegalStateException(
                    "La ruta debe tener un transportista válido antes de persistir. rutaId: " + ruta.getRutaId());
        }

        // Recuperar la entidad gestionada por JPA para que el FK se resuelva correctamente.
        TransportistaEntity transportistaEntity = transportistaJpaRepository
                .findByConductorId(ruta.getTransportista().getTransportistaId())
                .orElseThrow(() -> new IllegalStateException(
                        "Transportista no encontrado en BD para transportistaId: "
                        + ruta.getTransportista().getTransportistaId()));

        RutaEntity rutaEntity = RutaEntity.builder()
                .rutaId(ruta.getRutaId())
                .transportista(transportistaEntity)
                .vehiculoId(ruta.getVehiculoId())
                .tipoVehiculo(ruta.getTipoVehiculo() != null ? ruta.getTipoVehiculo().name() : null)
                .modeloContrato(ruta.getModeloContrato())
                .fechaInicioTransito(ruta.getFechaInicioTransito())
                .fechaCierre(ruta.getFechaCierre())
                .estadoProcesamiento(ruta.getEstadoProcesamiento())
                .build();

        List<ParadaEntity> paradas = (ruta.getParadas() == null ? List.<Parada>of() : ruta.getParadas())
                .stream()
                .map(paradaMapper::toEntity)
                .toList();

        paradas.forEach(rutaEntity::addParada);

        return rutaEntity;
    }

    public RutaCerrada toDomain(RutaEntity entity) {
        if (entity == null) return null;

        List<Parada> paradas = (entity.getParadas() == null ? List.<ParadaEntity>of() : entity.getParadas())
                .stream()
                .map(paradaMapper::toDomain)
                .toList();

        return RutaCerrada.builder()
                .rutaId(entity.getRutaId())
                .transportista(transportistaMapper.toDomain(entity.getTransportista()))
                .vehiculoId(entity.getVehiculoId())
                .tipoVehiculo(entity.getTipoVehiculo() != null ? TipoVehiculo.valueOf(entity.getTipoVehiculo()) : null)
                .modeloContrato(entity.getModeloContrato())
                .fechaInicioTransito(entity.getFechaInicioTransito())
                .fechaCierre(entity.getFechaCierre())
                .estadoProcesamiento(entity.getEstadoProcesamiento())
                .paradas(paradas)
                .build();
    }
}
