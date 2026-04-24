package com.logistica.domain.repositories;

import com.logistica.domain.models.Paquete;

import java.util.Optional;
import java.util.UUID;

public interface PaqueteRepository {
    Optional<Paquete> findById(UUID idPaquete);
    Paquete save(Paquete paquete);
}
