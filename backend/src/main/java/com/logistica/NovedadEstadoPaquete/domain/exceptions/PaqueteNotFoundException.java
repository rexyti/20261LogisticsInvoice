package com.logistica.NovedadEstadoPaquete.domain.exceptions;

public class PaqueteNotFoundException extends RuntimeException {

    public PaqueteNotFoundException(Long idPaquete) {
        super("Paquete no encontrado: " + idPaquete);
    }
}
