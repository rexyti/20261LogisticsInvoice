package com.logistica.domain.exceptions;

public class PagoNoEncontradoException extends RuntimeException {

    public PagoNoEncontradoException(String message) {
        super(message);
    }
}
