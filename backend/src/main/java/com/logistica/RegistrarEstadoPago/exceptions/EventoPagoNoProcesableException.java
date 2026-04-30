package com.logistica.RegistrarEstadoPago.exceptions;

public class EventoPagoNoProcesableException extends RuntimeException {
    public EventoPagoNoProcesableException(String mensaje) {
        super(mensaje);
    }
}
