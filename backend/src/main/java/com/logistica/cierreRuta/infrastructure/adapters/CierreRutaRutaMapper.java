package com.logistica.cierreRuta.infrastructure.adapters;

import com.logistica.cierreRuta.domain.enums.CierreRutaTipoVehiculo;
import com.logistica.cierreRuta.domain.models.Parada;
import com.logistica.cierreRuta.domain.models.CierreRutaRuta;
import com.logistica.cierreRuta.infrastructure.persistence.entities.ParadaEntity;
import com.logistica.cierreRuta.infrastructure.persistence.entities.RutaEntity;
import com.logistica.cierreRuta.infrastructure.persistence.entities.CierreRutaTransportistaEntity;
import com.logistica.cierreRuta.infrastructure.persistence.repositories.CierreRutaTransportistaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CierreRutaRutaMapper {

    private final TransportistaMapper transportistaMapper;
    private final CierreRutaTransportistaJpaRepository transportistaJpaRepository;
    private final ParadaMapper paradaMapper;

    public RutaEntity toEntity(CierreRutaRuta ruta) {
        if (ruta == null) return null;

        if (ruta.getTransportista() == null || ruta.getTransportista().getTransportistaId() == null) {
            throw new IllegalStateException(
                    "La ruta debe tener un transportista válido antes de persistir. rutaId: " + ruta.getRutaId());
        }

        // Recuperar la entidad gestionada por JPA para que el FK se resuelva correctamente.
        CierreRutaTransportistaEntity transportistaEntity = transportistaJpaRepository
                .findByConductorId(ruta.getTransportista().getTransportistaId())
                .orElseThrow(() -> new IllegalStateException(
                        "CierreRutaTransportista no encontrado en BD para transportistaId: "
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

    public CierreRutaRuta toDomain(RutaEntity entity) {
        if (entity == null) return null;

        List<Parada> paradas = (entity.getParadas() == null ? List.<ParadaEntity>of() : entity.getParadas())
                .stream()
                .map(paradaMapper::toDomain)
                .toList();

        return CierreRutaRuta.builder()
                .rutaId(entity.getRutaId())
                .transportista(transportistaMapper.toDomain(entity.getTransportista()))
                .vehiculoId(entity.getVehiculoId())
                .tipoVehiculo(entity.getTipoVehiculo() != null ? CierreRutaTipoVehiculo.valueOf(entity.getTipoVehiculo()) : null)
                .modeloContrato(entity.getModeloContrato())
                .fechaInicioTransito(entity.getFechaInicioTransito())
                .fechaCierre(entity.getFechaCierre())
                .estadoProcesamiento(entity.getEstadoProcesamiento())
                .paradas(paradas)
                .build();
    }
}
