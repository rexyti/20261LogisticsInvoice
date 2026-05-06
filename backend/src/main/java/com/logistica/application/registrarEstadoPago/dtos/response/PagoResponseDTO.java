package com.logistica.application.registrarEstadoPago.dtos.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PagoResponseDTO(
        UUID idPago,
        UUID idLiquidacion,
        RegistrarEstadoPagoEstadoPagoEnum estado,
        Instant fechaUltimaActualizacion,
        Long ultimaSecuenciaProcesada
) {}
