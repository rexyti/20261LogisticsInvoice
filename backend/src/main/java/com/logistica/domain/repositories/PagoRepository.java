package com.logistica.domain.repositories;

import com.logistica.domain.models.Pago;

import java.util.Optional;
import java.util.UUID;

public interface PagoRepository {
    Optional<Pago> findById(UUID idPago);
    Optional<Pago> findByIdLiquidacion(UUID idLiquidacion);
    Pago save(Pago pago);
}
