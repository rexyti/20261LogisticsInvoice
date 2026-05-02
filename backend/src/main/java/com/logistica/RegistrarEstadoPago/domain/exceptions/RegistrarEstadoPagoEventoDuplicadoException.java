package com.logistica.RegistrarEstadoPago.domain.exceptions;

public class RegistrarEstadoPagoEventoDuplicadoException extends RuntimeException {
    public RegistrarEstadoPagoEventoDuplicadoException(String idTransaccionBanco) {
        super("Evento duplicado detectado para idTransaccionBanco: " + idTransaccionBanco);
    }
}
