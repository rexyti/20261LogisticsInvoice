package com.logistica.NovedadEstadoPaquete.domain.exceptions;

public class EstadoNoMapeadoException extends RuntimeException {

    public EstadoNoMapeadoException(String estado) {
        super("Estado recibido no tiene regla de pago definida: " + estado);
    }
}
