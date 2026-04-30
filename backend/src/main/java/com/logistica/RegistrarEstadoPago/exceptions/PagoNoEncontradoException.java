package com.logistica.RegistrarEstadoPago.exceptions;

public class PagoNoEncontradoException extends RuntimeException {
    public PagoNoEncontradoException(String idPago) {
        super("Pago no encontrado: " + idPago);
    }
}
