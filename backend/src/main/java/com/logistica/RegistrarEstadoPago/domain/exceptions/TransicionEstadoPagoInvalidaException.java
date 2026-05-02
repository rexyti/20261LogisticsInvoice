package com.logistica.RegistrarEstadoPago.domain.exceptions;

public class TransicionEstadoPagoInvalidaException extends RuntimeException {
    public TransicionEstadoPagoInvalidaException(String from, String to) {
        super("Transición de estado inválida: " + from + " -> " + to);
    }
}
