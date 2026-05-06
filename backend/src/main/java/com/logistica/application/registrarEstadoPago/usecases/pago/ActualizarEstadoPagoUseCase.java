package com.logistica.application.registrarEstadoPago.usecases.pago;

import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

public interface ActualizarEstadoPagoUseCase {
    void actualizarEstadoPago(UUID idPago, RegistrarEstadoPagoEstadoPagoEnum nuevoEstado,
                               Instant fechaEvento, Long secuencia, UUID idEvento);
}
