package com.logistica.RegistrarEstadoPago.exceptions;

public class EventoDuplicadoException extends RuntimeException {
    public EventoDuplicadoException(String idTransaccionBanco) {
        super("Evento duplicado detectado para idTransaccionBanco: " + idTransaccionBanco);
    }
}
