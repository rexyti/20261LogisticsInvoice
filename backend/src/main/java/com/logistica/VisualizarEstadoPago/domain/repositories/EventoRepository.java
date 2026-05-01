package com.logistica.VisualizarEstadoPago.domain.repositories;

import com.logistica.VisualizarEstadoPago.domain.models.VisualizarEstadoPagoEventoTransaccion;

import java.util.Optional;
import java.util.UUID;

public interface EventoRepository {
    Optional<VisualizarEstadoPagoEventoTransaccion> findById(UUID id);
    VisualizarEstadoPagoEventoTransaccion save(VisualizarEstadoPagoEventoTransaccion eventoTransaccion);
}
