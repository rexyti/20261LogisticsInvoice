package com.logistica.application.registrarEstadoPago.dtos.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ActualizarEstadoPagoRequestDTO(
        @NotNull UUID idPago,
        @NotNull RegistrarEstadoPagoEstadoPagoEnum nuevoEstado
) {}
