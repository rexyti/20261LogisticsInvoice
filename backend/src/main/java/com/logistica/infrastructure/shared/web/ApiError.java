package com.logistica.infrastructure.shared.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.util.Map;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        String codigo,
        String mensaje,
        Map<String, String> detalles,
        Instant timestamp
) {
    public static ApiError of(String codigo, String mensaje) {
        return new ApiError(codigo, mensaje, null, Instant.now());
    }

    public static ApiError of(String codigo, String mensaje, Map<String, String> detalles) {
        return new ApiError(codigo, mensaje, detalles, Instant.now());
    }
}
