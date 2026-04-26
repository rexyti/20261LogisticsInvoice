package com.logistica.domain.repositories;

import com.logistica.domain.models.LiquidacionReferencia;

import java.util.Optional;
import java.util.UUID;

public interface LiquidacionRepository {
    Optional<LiquidacionReferencia> findById(UUID idLiquidacion);
    LiquidacionReferencia save(LiquidacionReferencia liquidacion);
}
