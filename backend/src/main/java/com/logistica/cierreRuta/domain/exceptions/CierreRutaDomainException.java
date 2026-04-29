package com.logistica.cierreRuta.domain.exceptions;

public class CierreRutaDomainException extends RuntimeException {
    public CierreRutaDomainException(String message) {
        super(message);
    }

    public CierreRutaDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
