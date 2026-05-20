package com.logistica.domain.conductor.exceptions;

import com.logistica.domain.shared.exceptions.DomainException;

import java.util.UUID;

public class ConductorInactivoException extends DomainException {

    public ConductorInactivoException(UUID conductorId) {
        super("El conductor con id " + conductorId + " no está habilitado para operar");
    }
}
