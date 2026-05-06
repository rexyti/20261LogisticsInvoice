package com.logistica.application.registrarEstadoPago.dtos.request;

import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record EventoEstadoPagoRequestDTO(
        @NotBlank String idEvento,
        @NotBlank String idTransaccionBanco,
        @NotNull UUID idPago,
        @NotNull UUID idLiquidacion,
        @NotNull RegistrarEstadoPagoEstadoPagoEnum estado,
        @NotNull LocalDateTime fechaEvento,
        Long secuencia,
        Map<String, Object> payloadOriginal
) {}
