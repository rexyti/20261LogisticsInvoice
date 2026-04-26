package com.logistica.application.usecases.pago;

import com.logistica.domain.enums.EstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

public interface RegistrarEstadoPagoUseCase {
    void registrarEstadoInicial(UUID idPago, UUID idLiquidacion, EstadoPagoEnum estado,
                                 Instant fechaEvento, Long secuencia, UUID idEvento);
}
