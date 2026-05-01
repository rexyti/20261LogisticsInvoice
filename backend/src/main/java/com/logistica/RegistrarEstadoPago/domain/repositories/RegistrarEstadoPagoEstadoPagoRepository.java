package com.logistica.RegistrarEstadoPago.domain.repositories;

import com.logistica.RegistrarEstadoPago.domain.models.RegistrarEstadoPagoEstadoPago;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistrarEstadoPagoEstadoPagoRepository {
    RegistrarEstadoPagoEstadoPago save(RegistrarEstadoPagoEstadoPago estadoPago);
    Optional<RegistrarEstadoPagoEstadoPago> findUltimoByIdPago(UUID idPago);
    List<RegistrarEstadoPagoEstadoPago> findAllByIdPago(UUID idPago);
}
