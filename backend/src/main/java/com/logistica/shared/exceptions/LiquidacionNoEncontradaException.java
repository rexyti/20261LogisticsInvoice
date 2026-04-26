package com.logistica.shared.exceptions;

public class LiquidacionNoEncontradaException extends RuntimeException {
    public LiquidacionNoEncontradaException(String idLiquidacion) {
        super("Liquidación no encontrada: " + idLiquidacion);
    }
}
