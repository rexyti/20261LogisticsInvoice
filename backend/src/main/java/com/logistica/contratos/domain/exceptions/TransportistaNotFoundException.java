package com.logistica.contratos.domain.exceptions;

import java.util.UUID;

public class TransportistaNotFoundException extends DomainException {

    public TransportistaNotFoundException(UUID transportistaId) {
        super("Transportista no encontrado con id: " + transportistaId);
    }
}