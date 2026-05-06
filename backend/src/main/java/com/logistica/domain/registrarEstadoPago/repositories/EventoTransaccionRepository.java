package com.logistica.domain.registrarEstadoPago.repositories;

import com.logistica.domain.registrarEstadoPago.models.RegistrarEstadoPagoEventoTransaccion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventoTransaccionRepository {
    RegistrarEstadoPagoEventoTransaccion save(RegistrarEstadoPagoEventoTransaccion evento);
    Optional<RegistrarEstadoPagoEventoTransaccion> findByIdTransaccionBanco(String idTransaccionBanco);
    List<RegistrarEstadoPagoEventoTransaccion> findByIdPago(UUID idPago);
}
