package com.logistica.domain.visualizarEstadoPago.repositories;

import com.logistica.domain.visualizarEstadoPago.models.VisualizarEstadoPagoEventoTransaccion;

import java.util.Optional;
import java.util.UUID;

public interface EventoRepository {
    Optional<VisualizarEstadoPagoEventoTransaccion> findById(UUID id);
    VisualizarEstadoPagoEventoTransaccion save(VisualizarEstadoPagoEventoTransaccion eventoTransaccion);
}
