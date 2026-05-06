package com.logistica.domain.visualizarEstadoPago.repositories;

import com.logistica.domain.visualizarEstadoPago.models.VisualizarEstadoPagoPago;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VisualizarEstadoPagoPagoRepository {
    Optional<VisualizarEstadoPagoPago> findById(UUID id);
    Optional<VisualizarEstadoPagoPago> findByIdAndUsuarioId(UUID id, UUID usuarioId);
    List<VisualizarEstadoPagoPago> findByUsuarioId(UUID usuarioId);
    VisualizarEstadoPagoPago save(VisualizarEstadoPagoPago pago);
}
