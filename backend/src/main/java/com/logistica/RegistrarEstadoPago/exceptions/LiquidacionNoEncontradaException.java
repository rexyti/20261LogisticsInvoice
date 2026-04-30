package com.logistica.RegistrarEstadoPago.exceptions;

public class LiquidacionNoEncontradaException extends RuntimeException {
    public LiquidacionNoEncontradaException(String idLiquidacion) {
        super("Liquidación no encontrada: " + idLiquidacion);
    }
}
