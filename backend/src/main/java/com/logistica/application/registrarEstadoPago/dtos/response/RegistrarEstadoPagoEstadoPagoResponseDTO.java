package com.logistica.application.registrarEstadoPago.dtos.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RegistrarEstadoPagoEstadoPagoResponseDTO(
        UUID idEstadoPago,
        UUID idPago,
        RegistrarEstadoPagoEstadoPagoEnum estado,
        Instant fechaRegistro,
        Instant fechaEventoBanco,
        Long secuenciaEvento
) {}
