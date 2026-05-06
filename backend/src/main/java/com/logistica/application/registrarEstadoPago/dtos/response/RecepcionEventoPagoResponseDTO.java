package com.logistica.application.registrarEstadoPago.dtos.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RecepcionEventoPagoResponseDTO(
        String mensaje,
        String idEvento,
        String idTransaccionBanco,
        String procesamiento
) {}
