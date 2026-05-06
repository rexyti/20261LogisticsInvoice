package com.logistica.domain.visualizarEstadoPago.exceptions;

public class AccessDeniedPaymentException extends RuntimeException {

    public AccessDeniedPaymentException(String message) {
        super(message);
    }
}
