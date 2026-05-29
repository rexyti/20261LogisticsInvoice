package com.logistica.domain.contratos.repositories;

import com.logistica.domain.contratos.models.Contrato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ContratoRepository {
    Optional<Contrato> buscarActivoPorTransportistaId(UUID transportistaId);
    Contrato guardar(Contrato contrato);
    boolean existePorIdContrato(String idContrato);
    Optional<Contrato> buscarPorIdContrato(String idContrato);
    Page<Contrato> listar(Pageable pageable);
    Page<Contrato> listarPorTransportista(UUID idTransportista, Pageable pageable);
}
