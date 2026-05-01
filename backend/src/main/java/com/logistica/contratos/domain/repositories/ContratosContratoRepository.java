package com.logistica.contratos.domain.repositories;

import com.logistica.contratos.domain.models.ContratosContrato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ContratosContratoRepository {
    ContratosContrato guardar(ContratosContrato contrato);
    Optional<ContratosContrato> buscarPorIdContrato(String idContrato);
    boolean existePorIdContrato(String idContrato);
    Page<ContratosContrato> listar(Pageable pageable);
}
