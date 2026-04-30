package com.logistica.RegistrarEstadoPago.domain.repositories;

import com.logistica.RegistrarEstadoPago.domain.models.EstadoPago;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstadoPagoRepository {
    EstadoPago save(EstadoPago estadoPago);
    Optional<EstadoPago> findUltimoByIdPago(UUID idPago);
    List<EstadoPago> findAllByIdPago(UUID idPago);
}
