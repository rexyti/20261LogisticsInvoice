package com.logistica.domain.models;

import com.logistica.domain.enums.EstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

public record EstadoPago(
        UUID idEstadoPago,
        UUID idPago,
        EstadoPagoEnum estado,
        Instant fechaRegistro,
        Instant fechaEventoBanco,
        Long secuenciaEvento,
        UUID idEventoTransaccion
) {}
