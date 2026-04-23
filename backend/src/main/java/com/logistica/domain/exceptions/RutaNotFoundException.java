package com.logistica.domain.exceptions;

import java.util.UUID;

public class RutaNotFoundException extends DomainException {
    public final UUID rutaId;
    public RutaNotFoundException(UUID rutaId) {
        super("Ruta no encontrada: " + rutaId);
        this.rutaId = rutaId;
    }
    public UUID getRutaId() {
        return rutaId;
    }
}
