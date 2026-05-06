package com.logistica.domain.registrarEstadoPago.repositories;

import com.logistica.domain.registrarEstadoPago.models.RegistrarEstadoPagoEstadoPago;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistrarEstadoPagoEstadoPagoRepository {
    RegistrarEstadoPagoEstadoPago save(RegistrarEstadoPagoEstadoPago estadoPago);
    Optional<RegistrarEstadoPagoEstadoPago> findUltimoByIdPago(UUID idPago);
    List<RegistrarEstadoPagoEstadoPago> findAllByIdPago(UUID idPago);
}
