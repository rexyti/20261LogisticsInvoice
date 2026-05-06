package com.logistica.domain.registrarEstadoPago.exceptions;

public class EventoPagoNoProcesableException extends RuntimeException {
    public EventoPagoNoProcesableException(String mensaje) {
        super(mensaje);
    }
}
