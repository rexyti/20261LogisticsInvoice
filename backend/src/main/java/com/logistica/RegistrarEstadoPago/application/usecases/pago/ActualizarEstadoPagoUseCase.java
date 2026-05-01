package com.logistica.RegistrarEstadoPago.application.usecases.pago;

import com.logistica.RegistrarEstadoPago.domain.enums.RegistrarEstadoPagoEstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

public interface ActualizarEstadoPagoUseCase {
    void actualizarEstadoPago(UUID idPago, RegistrarEstadoPagoEstadoPagoEnum nuevoEstado,
                               Instant fechaEvento, Long secuencia, UUID idEvento);
}
