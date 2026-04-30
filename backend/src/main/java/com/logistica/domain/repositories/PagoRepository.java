package com.logistica.domain.repositories;

import com.logistica.domain.models.Pago;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PagoRepository {
    Optional<Pago> findById(UUID id);
    Optional<Pago> findByIdAndUsuarioId(UUID id, UUID usuarioId);
    List<Pago> findByUsuarioId(UUID usuarioId);
    Pago save(Pago pago);
}
