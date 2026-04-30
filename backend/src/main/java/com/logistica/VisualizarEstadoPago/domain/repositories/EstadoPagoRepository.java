package com.logistica.VisualizarEstadoPago.domain.repositories;

import com.logistica.VisualizarEstadoPago.domain.models.EstadoPago;

import java.util.Optional;
import java.util.UUID;

public interface EstadoPagoRepository {
    Optional<EstadoPago> findById(UUID id);
    EstadoPago save(EstadoPago estadoPago);
}
