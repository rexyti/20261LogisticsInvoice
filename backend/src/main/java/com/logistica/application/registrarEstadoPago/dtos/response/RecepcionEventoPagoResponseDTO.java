package com.logistica.application.registrarEstadoPago.dtos.response;

public record RecepcionEventoPagoResponseDTO(
        String mensaje,
        String idEvento,
        String idTransaccionBanco,
        String procesamiento
) {}
