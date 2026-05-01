package com.logistica.RegistrarEstadoPago.application.usecases.pago;

import com.logistica.RegistrarEstadoPago.domain.enums.RegistrarEstadoPagoEstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

public interface RegistrarEstadoPagoUseCase {
    void registrarEstadoInicial(UUID idPago, UUID idLiquidacion, RegistrarEstadoPagoEstadoPagoEnum estado,
                                 Instant fechaEvento, Long secuencia, UUID idEvento);
}
