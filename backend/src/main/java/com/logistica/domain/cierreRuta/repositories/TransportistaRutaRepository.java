package com.logistica.domain.cierreRuta.repositories;

import com.logistica.domain.cierreRuta.models.TransportistaRuta;

import java.util.Optional;
import java.util.UUID;

public interface TransportistaRutaRepository {
    Optional<TransportistaRuta> buscarPorTransportistaId(UUID transportistaId);

    TransportistaRuta guardar(TransportistaRuta transportista);
}
