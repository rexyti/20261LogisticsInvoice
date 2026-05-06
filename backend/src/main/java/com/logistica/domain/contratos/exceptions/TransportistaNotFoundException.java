package com.logistica.domain.contratos.exceptions;

import com.logistica.domain.shared.exceptions.DomainException;

import java.util.UUID;

public class TransportistaNotFoundException extends DomainException {

    public TransportistaNotFoundException(UUID transportistaId) {
        super("Transportista no encontrado con id: " + transportistaId);
    }
}