package com.logistica.domain.novedadEstadoPaquete.exceptions;

public class PaqueteNotFoundException extends RuntimeException {

    public PaqueteNotFoundException(Long idPaquete) {
        super("NovedadEstadoPaquetePaquete no encontrado: " + idPaquete);
    }
}
