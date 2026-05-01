package com.logistica.VisualizarEstadoPago.domain.exceptions;

public class PagoNoEncontradoException extends RuntimeException {

    public PagoNoEncontradoException(String message) {
        super(message);
    }
}
