package com.logistica.RegistrarEstadoPago.application.dtos.response;

import com.logistica.RegistrarEstadoPago.domain.enums.RegistrarEstadoPagoEstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

public record RegistrarEstadoPagoEstadoPagoResponseDTO(
        UUID idEstadoPago,
        UUID idPago,
        RegistrarEstadoPagoEstadoPagoEnum estado,
        Instant fechaRegistro,
        Instant fechaEventoBanco,
        Long secuenciaEvento
) {}
