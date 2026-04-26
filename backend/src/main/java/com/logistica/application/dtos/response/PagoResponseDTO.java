package com.logistica.application.dtos.response;

import com.logistica.domain.enums.EstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

public record PagoResponseDTO(
        UUID idPago,
        UUID idLiquidacion,
        EstadoPagoEnum estado,
        Instant fechaUltimaActualizacion,
        Long ultimaSecuenciaProcesada
) {}
