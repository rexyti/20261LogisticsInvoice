package com.logistica.domain.visualizarEstadoPago.repositories;

import com.logistica.domain.visualizarEstadoPago.models.VisualizarEstadoPagoEstadoPago;

import java.util.Optional;
import java.util.UUID;

public interface VisualizarEstadoPagoEstadoPagoRepository {
    Optional<VisualizarEstadoPagoEstadoPago> findById(UUID id);
    VisualizarEstadoPagoEstadoPago save(VisualizarEstadoPagoEstadoPago estadoPago);
}
