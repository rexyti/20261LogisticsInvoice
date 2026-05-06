package com.logistica.domain.contratos.exceptions;

import com.logistica.domain.shared.exceptions.DomainException;

public class ContratoInvalidoException extends RuntimeException {
    public ContratoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
