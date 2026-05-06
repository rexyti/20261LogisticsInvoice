package com.logistica.domain.novedadEstadoPaquete.repositories;

import com.logistica.domain.novedadEstadoPaquete.models.NovedadEstadoPaquetePaquete;

import java.util.Optional;

public interface PaqueteRepository {

    Optional<NovedadEstadoPaquetePaquete> findByIdPaquete(Long idPaquete);

    NovedadEstadoPaquetePaquete save(NovedadEstadoPaquetePaquete paquete);
}
