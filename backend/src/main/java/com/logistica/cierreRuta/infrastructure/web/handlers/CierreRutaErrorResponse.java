package com.logistica.cierreRuta.infrastructure.web.handlers;

import java.time.LocalDateTime;
public record CierreRutaErrorResponse(
        String message,
        String code,
        int status,
        LocalDateTime timestamp
) {
}
