package com.logistica.application.registrarEstadoPago.dtos.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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
