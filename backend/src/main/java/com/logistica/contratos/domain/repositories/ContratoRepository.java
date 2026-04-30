package com.logistica.contratos.domain.repositories;

import com.logistica.contratos.domain.models.Contrato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ContratoRepository {
    Contrato guardar(Contrato contrato);
    Optional<Contrato> buscarPorIdContrato(String idContrato);
    boolean existePorIdContrato(String idContrato);
    Page<Contrato> listar(Pageable pageable);
}
