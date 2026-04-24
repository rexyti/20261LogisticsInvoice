package com.logistica.infrastructure.web.handlers;

import java.time.LocalDateTime;
public record ErrorResponse(
        String message,
        String code,
        int status,
        LocalDateTime timestamp
) {
}
