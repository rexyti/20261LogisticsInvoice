package com.logistica.VisualizarEstadoPago.infrastructure.adapters;

import com.logistica.VisualizarEstadoPago.domain.models.VisualizarEstadoPagoEventoTransaccion;
import com.logistica.VisualizarEstadoPago.infrastructure.persistence.entities.EventoEntity;
import org.springframework.stereotype.Component;

@Component
public class EventoMapper {

    public VisualizarEstadoPagoEventoTransaccion toDomain(EventoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new VisualizarEstadoPagoEventoTransaccion(
                entity.getId(),
                entity.getTipo(),
                entity.getFecha(),
                entity.getDatos()
        );
    }

    public EventoEntity toEntity(VisualizarEstadoPagoEventoTransaccion domain) {
        if (domain == null) {
            return null;
        }
        return new EventoEntity(
                domain.getId(),
                domain.getTipo(),
                domain.getFecha(),
                domain.getDatos()
        );
    }
}
