package com.logistica.application.registrarEstadoPago.dtos.response;

import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

public record PagoResponseDTO(
        UUID idPago,
        UUID idLiquidacion,
        RegistrarEstadoPagoEstadoPagoEnum estado,
        Instant fechaUltimaActualizacion,
        Long ultimaSecuenciaProcesada
) {}
