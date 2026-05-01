package com.logistica.RegistrarEstadoPago.application.dtos.response;

import com.logistica.RegistrarEstadoPago.domain.enums.RegistrarEstadoPagoEstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

public record PagoResponseDTO(
        UUID idPago,
        UUID idLiquidacion,
        RegistrarEstadoPagoEstadoPagoEnum estado,
        Instant fechaUltimaActualizacion,
        Long ultimaSecuenciaProcesada
) {}
