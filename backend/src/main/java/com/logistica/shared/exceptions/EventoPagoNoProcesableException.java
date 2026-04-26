package com.logistica.shared.exceptions;

public class EventoPagoNoProcesableException extends RuntimeException {
    public EventoPagoNoProcesableException(String mensaje) {
        super(mensaje);
    }
}
