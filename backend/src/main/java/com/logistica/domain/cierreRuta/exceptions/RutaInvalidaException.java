package com.logistica.domain.cierreRuta.exceptions;

import com.logistica.domain.shared.exceptions.DomainException;

public class RutaInvalidaException extends DomainException {
    public RutaInvalidaException(String message) {
        super(message);
    }
}