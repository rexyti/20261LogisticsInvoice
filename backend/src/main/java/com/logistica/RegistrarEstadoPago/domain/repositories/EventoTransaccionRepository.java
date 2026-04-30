package com.logistica.RegistrarEstadoPago.domain.repositories;

import com.logistica.RegistrarEstadoPago.domain.models.EventoTransaccion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventoTransaccionRepository {
    EventoTransaccion save(EventoTransaccion evento);
    Optional<EventoTransaccion> findByIdTransaccionBanco(String idTransaccionBanco);
    List<EventoTransaccion> findByIdPago(UUID idPago);
}
