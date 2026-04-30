package com.logistica.RegistrarEstadoPago.application.dtos.response;

import com.logistica.RegistrarEstadoPago.domain.enums.EstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

public record EstadoPagoResponseDTO(
        UUID idEstadoPago,
        UUID idPago,
        EstadoPagoEnum estado,
        Instant fechaRegistro,
        Instant fechaEventoBanco,
        Long secuenciaEvento
) {}
