package com.logistica.domain.registrarEstadoPago.exceptions;

import com.logistica.domain.shared.exceptions.DomainException;

public class ErrorComunicacionBancoException extends DomainException {

    public ErrorComunicacionBancoException(String detalle) {
        super("Error al comunicarse con el servicio bancario: " + detalle);
    }
}
