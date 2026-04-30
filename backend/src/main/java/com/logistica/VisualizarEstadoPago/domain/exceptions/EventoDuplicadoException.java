package com.logistica.VisualizarEstadoPago.domain.exceptions;

public class EventoDuplicadoException extends RuntimeException {

    public EventoDuplicadoException(String message) {
        super(message);
    }
}
