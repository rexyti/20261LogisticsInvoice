package com.logistica.shared.exceptions;

import java.util.UUID;

public class PaqueteNoEncontradoException extends RuntimeException {
    private final UUID idPaquete;

    public PaqueteNoEncontradoException(UUID idPaquete) {
        super("Paquete no encontrado: " + idPaquete);
        this.idPaquete = idPaquete;
    }

    public UUID getIdPaquete() {
        return idPaquete;
    }
}
