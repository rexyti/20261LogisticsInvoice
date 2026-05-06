package com.logistica.application.registrarEstadoPago.dtos.response;

import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;

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
