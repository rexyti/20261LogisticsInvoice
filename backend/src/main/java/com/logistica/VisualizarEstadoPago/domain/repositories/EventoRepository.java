package com.logistica.VisualizarEstadoPago.domain.repositories;

import com.logistica.VisualizarEstadoPago.domain.models.EventoTransaccion;

import java.util.Optional;
import java.util.UUID;

public interface EventoRepository {
    Optional<EventoTransaccion> findById(UUID id);
    EventoTransaccion save(EventoTransaccion eventoTransaccion);
}
