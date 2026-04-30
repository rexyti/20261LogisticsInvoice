package com.logistica.contratos.domain.repositories;

import com.logistica.contratos.domain.models.Transportista;

import java.util.Optional;
import java.util.UUID;

public interface TransportistaRepository {
    Transportista guardar(Transportista transportista);
    Optional<Transportista> buscarPorId(UUID id);
}
