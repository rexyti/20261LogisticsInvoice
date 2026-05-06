package com.logistica.domain.registrarEstadoPago.exceptions;

public class RegistrarEstadoPagoEventoDuplicadoException extends RuntimeException {
    public RegistrarEstadoPagoEventoDuplicadoException(String idTransaccionBanco) {
        super("Evento duplicado detectado para idTransaccionBanco: " + idTransaccionBanco);
    }
}
