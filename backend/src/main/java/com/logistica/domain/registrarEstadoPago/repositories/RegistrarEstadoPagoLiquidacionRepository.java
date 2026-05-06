package com.logistica.domain.registrarEstadoPago.repositories;

import com.logistica.domain.registrarEstadoPago.models.LiquidacionReferencia;

import java.util.Optional;
import java.util.UUID;

public interface RegistrarEstadoPagoLiquidacionRepository {
    Optional<LiquidacionReferencia> findById(UUID idLiquidacion);
    LiquidacionReferencia save(LiquidacionReferencia liquidacion);
}
