package com.logistica.infrastructure.adapters;

import com.logistica.domain.models.EventoTransaccion;
import com.logistica.infrastructure.persistence.entities.EventoEntity;
import org.springframework.stereotype.Component;

@Component
public class EventoMapper {

    public EventoTransaccion toDomain(EventoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new EventoTransaccion(
                entity.getId(),
                entity.getTipo(),
                entity.getFecha(),
                entity.getDatos()
        );
    }

    public EventoEntity toEntity(EventoTransaccion domain) {
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
