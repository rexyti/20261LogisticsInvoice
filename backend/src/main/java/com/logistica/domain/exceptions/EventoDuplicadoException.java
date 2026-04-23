package com.logistica.domain.exceptions;

import java.util.UUID;

public class EventoDuplicadoException extends DomainException {
    public EventoDuplicadoException(UUID rutaId) {
        super("Evento duplicado para ruta_id: " + rutaId);
    }
}
