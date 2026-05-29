package com.logistica.domain.conductor.exceptions;

import com.logistica.domain.shared.exceptions.DomainException;

import java.util.UUID;

public class ConductorNoEncontradoException extends DomainException {

    public ConductorNoEncontradoException(UUID conductorId) {
        super("El conductor con id " + conductorId + " no fue encontrado");
    }
}
