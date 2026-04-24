package com.logistica.domain.repositories;

import com.logistica.domain.models.Paquete;

import java.util.Optional;

public interface PaqueteRepository {

    Optional<Paquete> findByIdPaquete(Long idPaquete);

    Paquete save(Paquete paquete);
}
