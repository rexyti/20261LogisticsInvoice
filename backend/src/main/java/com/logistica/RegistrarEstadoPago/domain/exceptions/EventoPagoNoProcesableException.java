package com.logistica.RegistrarEstadoPago.domain.exceptions;

public class EventoPagoNoProcesableException extends RuntimeException {
    public EventoPagoNoProcesableException(String mensaje) {
        super(mensaje);
    }
}
