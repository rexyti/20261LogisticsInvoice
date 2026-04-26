package com.logistica.application.dtos.response;

public record RecepcionEventoPagoResponseDTO(
        String mensaje,
        String idEvento,
        String idTransaccionBanco,
        String procesamiento
) {}
