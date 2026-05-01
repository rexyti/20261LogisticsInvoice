package com.logistica.NovedadEstadoPaquete.domain.repositories;

import com.logistica.NovedadEstadoPaquete.domain.models.Paquete;

import java.util.Optional;

public interface PaqueteRepository {

    Optional<Paquete> findByIdPaquete(Long idPaquete);

    Paquete save(Paquete paquete);
}
