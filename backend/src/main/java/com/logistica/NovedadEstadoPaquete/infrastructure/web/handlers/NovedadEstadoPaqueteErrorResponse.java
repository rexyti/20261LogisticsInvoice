package com.logistica.NovedadEstadoPaquete.infrastructure.web.handlers;

import java.time.LocalDateTime;

public record NovedadEstadoPaqueteErrorResponse(
        int           status,
        String        error,
        String        message,
        LocalDateTime timestamp
) {}
