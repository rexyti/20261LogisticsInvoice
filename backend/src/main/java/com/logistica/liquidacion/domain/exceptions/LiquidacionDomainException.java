package com.logistica.liquidacion.domain.exceptions;

import org.springframework.http.HttpStatus;

public abstract class LiquidacionDomainException extends RuntimeException {

    private final HttpStatus status;

    public LiquidacionDomainException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
