package com.logistica.infrastructure.visualizarEstadoPago.adapters;

import com.logistica.domain.visualizarEstadoPago.models.VisualizarEstadoPagoEstadoPago;
import com.logistica.infrastructure.visualizarEstadoPago.persistence.entities.VisualizarEstadoPagoEstadoPagoEntity;
import org.springframework.stereotype.Component;

@Component
public class EstadoPagoMapper {

    public VisualizarEstadoPagoEstadoPago toDomain(VisualizarEstadoPagoEstadoPagoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new VisualizarEstadoPagoEstadoPago(
                entity.getIdEstadoPago(),
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
