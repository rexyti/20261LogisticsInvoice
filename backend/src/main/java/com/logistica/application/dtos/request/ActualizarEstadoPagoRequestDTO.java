package com.logistica.application.dtos.request;

import com.logistica.domain.enums.EstadoPagoEnum;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ActualizarEstadoPagoRequestDTO(
        @NotNull UUID idPago,
        @NotNull EstadoPagoEnum nuevoEstado
) {}
