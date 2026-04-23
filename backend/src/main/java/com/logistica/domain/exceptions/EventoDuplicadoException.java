package com.logistica.domain.exceptions;

import java.util.UUID;

public class EventoDuplicadoException extends DomainException {
    private static final String MESSAGE = "Ruta no encontrada: ";


    public EventoDuplicadoException(UUID rutaId) {
        super( MESSAGE + rutaId);
    }
}
