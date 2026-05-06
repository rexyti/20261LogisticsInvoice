package com.logistica.domain.registrarEstadoPago.models;

import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

public record RegistrarEstadoPagoEstadoPago(
        UUID idEstadoPago,
        UUID idPago,
        RegistrarEstadoPagoEstadoPagoEnum estado,
        Instant fechaRegistro,
        Instant fechaEventoBanco,
        Long secuenciaEvento,
        UUID idEventoTransaccion
) {}
