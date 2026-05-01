package com.logistica.RegistrarEstadoPago.application.dtos.request;

import com.logistica.RegistrarEstadoPago.domain.enums.RegistrarEstadoPagoEstadoPagoEnum;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ActualizarEstadoPagoRequestDTO(
        @NotNull UUID idPago,
        @NotNull RegistrarEstadoPagoEstadoPagoEnum nuevoEstado
) {}
