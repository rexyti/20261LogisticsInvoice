package com.logistica.RegistrarEstadoPago.domain.repositories;

import com.logistica.RegistrarEstadoPago.domain.models.RegistrarEstadoPagoPago;

import java.util.Optional;
import java.util.UUID;

public interface RegistrarEstadoPagoPagoRepository {
    Optional<RegistrarEstadoPagoPago> findById(UUID idPago);
    Optional<RegistrarEstadoPagoPago> findByIdLiquidacion(UUID idLiquidacion);
    RegistrarEstadoPagoPago save(RegistrarEstadoPagoPago pago);
}
