package com.logistica.cierreRuta.domain.exceptions;

import java.util.UUID;

public class EventoDuplicadoException extends CierreRutaDomainException {
    private static final String MESSAGE = "Evento duplicado, ruta procesada: ";


    public EventoDuplicadoException(UUID rutaId) {
        super( MESSAGE + rutaId);
    }
}
