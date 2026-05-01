package com.logistica.RegistrarEstadoPago.exceptions;

public class RegistrarEstadoPagoPagoNoEncontradoException extends RuntimeException {
    public RegistrarEstadoPagoPagoNoEncontradoException(String idPago) {
        super("RegistrarEstadoPagoPago no encontrado: " + idPago);
    }
}
