package com.logistica.domain.cierreRuta.exceptions;

import com.logistica.domain.shared.exceptions.DomainException;

import java.util.UUID;

public class RutaNotFoundException extends DomainException {
    private final UUID rutaId;
    public RutaNotFoundException(UUID rutaId) {
        super("Ruta no encontrada: " + rutaId);
        this.rutaId = rutaId;
    }
    public UUID getRutaId() {
        return rutaId;
    }
}
