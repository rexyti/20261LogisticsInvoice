package com.logistica.RegistrarEstadoPago.domain.repositories;

import com.logistica.RegistrarEstadoPago.domain.models.LiquidacionReferencia;

import java.util.Optional;
import java.util.UUID;

public interface RegistrarEstadoPagoLiquidacionRepository {
    Optional<LiquidacionReferencia> findById(UUID idLiquidacion);
    LiquidacionReferencia save(LiquidacionReferencia liquidacion);
}
