package com.logistica.infrastructure.visualizarEstadoPago.adapters;

import com.logistica.domain.visualizarEstadoPago.models.VisualizarEstadoPagoEventoTransaccion;
import com.logistica.infrastructure.visualizarEstadoPago.persistence.entities.EventoEntity;
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
