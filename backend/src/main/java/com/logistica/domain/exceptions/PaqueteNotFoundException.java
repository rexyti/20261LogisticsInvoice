package com.logistica.domain.exceptions;

public class PaqueteNotFoundException extends RuntimeException {

    public PaqueteNotFoundException(Long idPaquete) {
        super("Paquete no encontrado: " + idPaquete);
    }
}
