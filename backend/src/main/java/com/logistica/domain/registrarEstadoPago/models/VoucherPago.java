package com.logistica.domain.registrarEstadoPago.models;

import java.time.Instant;

public record VoucherPago(
        String numeroVoucher,
        Instant fechaProcesamiento
) {}
