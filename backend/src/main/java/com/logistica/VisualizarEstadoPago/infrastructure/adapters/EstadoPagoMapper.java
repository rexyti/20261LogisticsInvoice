package com.logistica.VisualizarEstadoPago.infrastructure.adapters;

import com.logistica.VisualizarEstadoPago.domain.models.VisualizarEstadoPagoEstadoPago;
import com.logistica.VisualizarEstadoPago.infrastructure.persistence.entities.VisualizarEstadoPagoEstadoPagoEntity;
import org.springframework.stereotype.Component;

@Component
public class EstadoPagoMapper {

    public VisualizarEstadoPagoEstadoPago toDomain(VisualizarEstadoPagoEstadoPagoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new VisualizarEstadoPagoEstadoPago(
                entity.getId(),
                entity.getPagoId(),
                entity.getEstado()
        );
    }

    public VisualizarEstadoPagoEstadoPagoEntity toEntity(VisualizarEstadoPagoEstadoPago domain) {
        if (domain == null) {
            return null;
        }
        return new VisualizarEstadoPagoEstadoPagoEntity(
                domain.getId(),
                domain.getPagoId(),
                domain.getEstado()
        );
    }
}
