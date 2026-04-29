package com.logistica.cierreRuta.domain.exceptions;

import java.util.UUID;

public class RutaNotFoundException extends CierreRutaDomainException {
    private final UUID rutaId;
    public RutaNotFoundException(UUID rutaId) {
        super("Ruta no encontrada: " + rutaId);
        this.rutaId = rutaId;
    }
    public UUID getRutaId() {
        return rutaId;
    }
}
