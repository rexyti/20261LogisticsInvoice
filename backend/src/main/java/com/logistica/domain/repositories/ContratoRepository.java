package com.logistica.domain.repositories;

import com.logistica.domain.models.Contrato;

import java.util.List;
import java.util.Optional;

public interface ContratoRepository {
    Contrato guardar(Contrato contrato);
    Optional<Contrato> buscarPorIdContrato(String idContrato);
    boolean existePorIdContrato(String idContrato);
    List<Contrato> listar();
}
