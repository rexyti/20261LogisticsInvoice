package com.logistica.domain.cierreRuta.exceptions;

import com.logistica.domain.shared.exceptions.DomainException;



public class ParadaInvalidaException extends DomainException {
    public ParadaInvalidaException(String message) {
        super(message);
    }
}