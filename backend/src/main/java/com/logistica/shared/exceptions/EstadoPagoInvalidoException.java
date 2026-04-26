package com.logistica.shared.exceptions;

public class EstadoPagoInvalidoException extends RuntimeException {
    public EstadoPagoInvalidoException(String estado) {
        super("Estado de pago inválido: " + estado);
    }
}
