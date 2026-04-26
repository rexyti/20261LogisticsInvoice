package com.logistica.application.usecases.pago;

import com.logistica.domain.enums.EstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

public interface ActualizarEstadoPagoUseCase {
    void actualizarEstadoPago(UUID idPago, EstadoPagoEnum nuevoEstado,
                               Instant fechaEvento, Long secuencia, UUID idEvento);
}
