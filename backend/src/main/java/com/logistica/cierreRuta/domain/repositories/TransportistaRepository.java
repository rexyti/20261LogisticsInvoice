package com.logistica.cierreRuta.domain.repositories;

import com.logistica.cierreRuta.domain.models.Transportista;

import java.util.Optional;
import java.util.UUID;

public interface TransportistaRepository {
    Optional<Transportista> buscarPorTransportistaId(UUID transportistaId);

    Transportista guardar(Transportista transportista);
}
