package com.logistica.infrastructure.visualizarEstadoPago.adapters;

import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;
import com.logistica.domain.visualizarEstadoPago.models.VisualizarEstadoPagoEstadoPago;
import com.logistica.infrastructure.registrarEstadoPago.persistence.entities.RegistrarEstadoPagoEstadoPagoEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class EstadoPagoMapper {

    public VisualizarEstadoPagoEstadoPago toDomain(RegistrarEstadoPagoEstadoPagoEntity entity) {
        if (entity == null) return null;
        return new VisualizarEstadoPagoEstadoPago(
                entity.getIdEstadoPago(),
                entity.getIdPago(),
                entity.getEstado() != null ? entity.getEstado().name() : null
        );
    }

    public RegistrarEstadoPagoEstadoPagoEntity toEntity(VisualizarEstadoPagoEstadoPago domain) {
        if (domain == null) return null;
        return RegistrarEstadoPagoEstadoPagoEntity.builder()
                .idEstadoPago(domain.getId())
                .idPago(domain.getPagoId())
                .estado(domain.getEstado() != null
                        ? RegistrarEstadoPagoEstadoPagoEnum.valueOf(domain.getEstado())
                        : null)
                .fechaRegistro(Instant.now())
                .build();
    }
}
