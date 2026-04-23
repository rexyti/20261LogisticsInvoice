package com.logistica.domain.exceptions;

import java.util.UUID;

public class RutaNotFoundException extends DomainException {
    public RutaNotFoundException(UUID rutaId) {
        super("Ruta no encontrada: " + rutaId);
    }
}
