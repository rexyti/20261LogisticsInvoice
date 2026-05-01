package com.logistica.NovedadEstadoPaquete.infrastructure.web.handlers;

import java.time.LocalDateTime;

public record ErrorResponse(
        int           status,
        String        error,
        String        message,
        LocalDateTime timestamp
) {}
