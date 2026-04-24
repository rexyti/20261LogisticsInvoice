package com.logistica.shared.exceptions;

import java.util.UUID;

public class PendienteSincronizacionException extends RuntimeException {
    private final UUID idPaquete;

    public PendienteSincronizacionException(UUID idPaquete, Throwable cause) {
        super("Sincronización pendiente para paquete: " + idPaquete, cause);
        this.idPaquete = idPaquete;
    }

    public UUID getIdPaquete() {
        return idPaquete;
    }
}
