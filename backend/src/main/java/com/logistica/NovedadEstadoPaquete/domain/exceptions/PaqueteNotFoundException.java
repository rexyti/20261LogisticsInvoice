package com.logistica.NovedadEstadoPaquete.domain.exceptions;

public class PaqueteNotFoundException extends RuntimeException {

    public PaqueteNotFoundException(Long idPaquete) {
        super("NovedadEstadoPaquetePaquete no encontrado: " + idPaquete);
    }
}
