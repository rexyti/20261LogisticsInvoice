package com.logistica.shared.exceptions;

public class EventoDuplicadoException extends RuntimeException {
    public EventoDuplicadoException(String idTransaccionBanco) {
        super("Evento duplicado detectado para idTransaccionBanco: " + idTransaccionBanco);
    }
}
