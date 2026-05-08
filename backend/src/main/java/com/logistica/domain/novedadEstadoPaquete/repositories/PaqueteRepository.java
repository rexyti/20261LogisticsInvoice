package com.logistica.domain.novedadEstadoPaquete.repositories;

import com.logistica.domain.novedadEstadoPaquete.models.NovedadEstadoPaquetePaquete;

import java.util.Optional;
import java.util.UUID;

public interface PaqueteRepository {

    Optional<NovedadEstadoPaquetePaquete> findByIdPaquete(UUID idPaquete);

    NovedadEstadoPaquetePaquete save(NovedadEstadoPaquetePaquete paquete);
}
