package com.logistica.application.registrarEstadoPago.dtos.request;

import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ActualizarEstadoPagoRequestDTO(
        @NotNull UUID idPago,
        @NotNull RegistrarEstadoPagoEstadoPagoEnum nuevoEstado
) {}
