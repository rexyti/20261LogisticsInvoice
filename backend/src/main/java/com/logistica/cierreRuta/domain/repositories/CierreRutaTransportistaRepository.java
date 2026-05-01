package com.logistica.cierreRuta.domain.repositories;

import com.logistica.cierreRuta.domain.models.CierreRutaTransportista;

import java.util.Optional;
import java.util.UUID;

public interface CierreRutaTransportistaRepository {
    Optional<CierreRutaTransportista> buscarPorTransportistaId(UUID transportistaId);

    CierreRutaTransportista guardar(CierreRutaTransportista transportista);
}
