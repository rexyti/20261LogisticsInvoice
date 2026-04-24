package com.logistica.liquidacion.domain.repositories;

import com.logistica.liquidacion.domain.models.Contrato;

import java.util.Optional;
import java.util.UUID;

public interface ContratoRepository {
    Contrato save(Contrato contrato);
    Optional<Contrato> findById(UUID id);
}