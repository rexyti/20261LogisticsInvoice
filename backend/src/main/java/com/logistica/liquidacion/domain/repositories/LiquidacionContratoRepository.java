package com.logistica.liquidacion.domain.repositories;

import com.logistica.liquidacion.domain.models.LiquidacionContrato;

import java.util.Optional;
import java.util.UUID;

public interface LiquidacionContratoRepository {
    LiquidacionContrato save(LiquidacionContrato contrato);
    Optional<LiquidacionContrato> findById(UUID id);
}