package com.logistica.domain.cierreRuta.exceptions;

import com.logistica.domain.shared.exceptions.DomainException;

import java.util.UUID;

public class EventoDuplicadoException extends DomainException {
    private static final String MESSAGE = "Evento duplicado, ruta procesada: ";


    public EventoDuplicadoException(UUID rutaId) {
        super( MESSAGE + rutaId);
    }
}
