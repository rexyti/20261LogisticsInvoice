package com.logistica.domain.registrarEstadoPago.repositories;

import com.logistica.domain.registrarEstadoPago.models.RegistrarEstadoPagoPago;

import java.util.Optional;
import java.util.UUID;

public interface RegistrarEstadoPagoPagoRepository {
    Optional<RegistrarEstadoPagoPago> findById(UUID idPago);
    Optional<RegistrarEstadoPagoPago> findByIdLiquidacion(UUID idLiquidacion);
    RegistrarEstadoPagoPago save(RegistrarEstadoPagoPago pago);
}
