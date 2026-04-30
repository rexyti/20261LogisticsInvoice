package com.logistica.RegistrarEstadoPago.application.usecases.pago;

import com.logistica.RegistrarEstadoPago.domain.enums.EstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

public interface ActualizarEstadoPagoUseCase {
    void actualizarEstadoPago(UUID idPago, EstadoPagoEnum nuevoEstado,
                               Instant fechaEvento, Long secuencia, UUID idEvento);
}
