package com.logistica.VisualizarEstadoPago.domain.exceptions;

public class AccessDeniedPaymentException extends RuntimeException {

    public AccessDeniedPaymentException(String message) {
        super(message);
    }
}
