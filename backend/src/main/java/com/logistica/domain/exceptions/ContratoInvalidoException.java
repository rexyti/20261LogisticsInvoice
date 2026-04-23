package com.logistica.domain.exceptions;

public class ContratoInvalidoException extends RuntimeException {
    public ContratoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
