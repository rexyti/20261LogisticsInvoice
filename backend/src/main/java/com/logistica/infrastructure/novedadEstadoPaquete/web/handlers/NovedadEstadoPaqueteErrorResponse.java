package com.logistica.infrastructure.novedadEstadoPaquete.web.handlers;

import java.time.LocalDateTime;

public record NovedadEstadoPaqueteErrorResponse(
        int           status,
        String        error,
        String        message,
        LocalDateTime timestamp
) {}
