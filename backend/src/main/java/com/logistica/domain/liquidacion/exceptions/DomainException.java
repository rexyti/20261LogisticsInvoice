package com.logistica.domain.liquidacion.exceptions;

import org.springframework.http.HttpStatus;

public abstract class DomainException extends com.logistica.domain.shared.exceptions.DomainException {

    private final HttpStatus status;

    public DomainException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
