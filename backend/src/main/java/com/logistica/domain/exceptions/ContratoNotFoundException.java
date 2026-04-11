package com.logistica.domain.exceptions;

public class ContratoNotFoundException extends RuntimeException {
    public ContratoNotFoundException(String message) {
        super(message);
    }
}
