package com.logistica.domain.exceptions;

import java.util.UUID;

public class ParadaInvalidaException extends DomainException {

    public ParadaInvalidaException(UUID paradaId) {
        super("Parada inválida: " + paradaId);
    }

    public ParadaInvalidaException(String message) {
        super(message);
    }
}