package com.logistica.domain.repositories;

import com.logistica.domain.models.EventoTransaccion;

import java.util.Optional;
import java.util.UUID;

public interface EventoRepository {
    Optional<EventoTransaccion> findById(UUID id);
    EventoTransaccion save(EventoTransaccion eventoTransaccion);
}
