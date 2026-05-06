package com.logistica.domain.contratos.exceptions;

import com.logistica.domain.shared.exceptions.DomainException;

public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
