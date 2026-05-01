package com.logistica.contratos.domain.repositories;

import com.logistica.contratos.domain.models.ContratosTransportista;

import java.util.Optional;
import java.util.UUID;

public interface ContratosTransportistaRepository {
    ContratosTransportista guardar(ContratosTransportista transportista);
    Optional<ContratosTransportista> buscarPorId(UUID id);
}
