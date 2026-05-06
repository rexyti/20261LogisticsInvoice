package com.logistica.domain.liquidacion.repositories;

import com.logistica.domain.liquidacion.models.ContratoTarifa;

import java.util.Optional;
import java.util.UUID;

public interface ContratoTarifaRepository {
    ContratoTarifa save(ContratoTarifa contrato);
    Optional<ContratoTarifa> findById(UUID id);
}
