package com.logistica.RegistrarEstadoPago.domain.exceptions;

public class RegistrarEstadoPagoPagoNoEncontradoException extends RuntimeException {
    public RegistrarEstadoPagoPagoNoEncontradoException(String idPago) {
        super("RegistrarEstadoPagoPago no encontrado: " + idPago);
    }
}
