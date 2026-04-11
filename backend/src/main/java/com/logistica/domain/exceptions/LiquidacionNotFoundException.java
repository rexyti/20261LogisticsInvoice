package com.logistica.domain.exceptions;

public class LiquidacionNotFoundException extends RuntimeException {
    public LiquidacionNotFoundException(String message) {
        super(message);
    }
}
