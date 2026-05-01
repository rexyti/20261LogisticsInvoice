package com.logistica.VisualizarEstadoPago.domain.repositories;

import com.logistica.VisualizarEstadoPago.domain.models.VisualizarEstadoPagoEstadoPago;

import java.util.Optional;
import java.util.UUID;

public interface VisualizarEstadoPagoEstadoPagoRepository {
    Optional<VisualizarEstadoPagoEstadoPago> findById(UUID id);
    VisualizarEstadoPagoEstadoPago save(VisualizarEstadoPagoEstadoPago estadoPago);
}
