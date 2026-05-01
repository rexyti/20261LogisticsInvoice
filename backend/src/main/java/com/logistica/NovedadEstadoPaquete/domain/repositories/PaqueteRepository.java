package com.logistica.NovedadEstadoPaquete.domain.repositories;

import com.logistica.NovedadEstadoPaquete.domain.models.NovedadEstadoPaquetePaquete;

import java.util.Optional;

public interface PaqueteRepository {

    Optional<NovedadEstadoPaquetePaquete> findByIdPaquete(Long idPaquete);

    NovedadEstadoPaquetePaquete save(NovedadEstadoPaquetePaquete paquete);
}
