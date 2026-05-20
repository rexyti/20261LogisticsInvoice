package com.logistica.domain.registrarEstadoPago.exceptions;

import com.logistica.domain.shared.exceptions.DomainException;

import java.util.UUID;

public class PagoRechazadoBancoException extends DomainException {

    public PagoRechazadoBancoException(UUID idLiquidacion, String motivo) {
        super("El pago para la liquidación " + idLiquidacion + " fue rechazado por el banco. Motivo: " + motivo);
    }
}
