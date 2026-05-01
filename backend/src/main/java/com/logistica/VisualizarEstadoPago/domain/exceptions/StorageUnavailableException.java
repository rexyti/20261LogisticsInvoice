package com.logistica.VisualizarEstadoPago.domain.exceptions;

public class StorageUnavailableException extends RuntimeException {

    public StorageUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
