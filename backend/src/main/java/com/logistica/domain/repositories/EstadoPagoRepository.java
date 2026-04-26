package com.logistica.domain.repositories;

import com.logistica.domain.models.EstadoPago;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstadoPagoRepository {
    EstadoPago save(EstadoPago estadoPago);
    Optional<EstadoPago> findUltimoByIdPago(UUID idPago);
    List<EstadoPago> findAllByIdPago(UUID idPago);
}
