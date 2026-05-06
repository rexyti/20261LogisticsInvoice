package com.logistica.domain.registrarEstadoPago.exceptions;

public class TransicionEstadoPagoInvalidaException extends RuntimeException {
    public TransicionEstadoPagoInvalidaException(String from, String to) {
        super("Transición de estado inválida: " + from + " -> " + to);
    }
}
