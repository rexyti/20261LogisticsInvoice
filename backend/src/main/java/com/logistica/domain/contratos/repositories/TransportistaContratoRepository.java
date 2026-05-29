package com.logistica.domain.contratos.repositories;

import com.logistica.domain.contratos.models.Transportista;

import java.util.Optional;
import java.util.UUID;

public interface TransportistaContratoRepository {
    Optional<Transportista> buscarPorId(UUID id);
    Transportista guardar(Transportista transportista);
}
