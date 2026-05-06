package com.logistica.application.registrarEstadoPago.usecases.pago;

import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

public interface RegistrarEstadoPagoUseCase {
    void registrarEstadoInicial(UUID idPago, UUID idLiquidacion, RegistrarEstadoPagoEstadoPagoEnum estado,
                                 Instant fechaEvento, Long secuencia, UUID idEvento);
}
