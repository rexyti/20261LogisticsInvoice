package com.logistica.domain.registrarEstadoPago.ports;

import com.logistica.domain.registrarEstadoPago.models.VoucherPago;

import java.math.BigDecimal;
import java.util.UUID;

public interface BancoGateway {
    VoucherPago registrarPago(UUID idLiquidacion, BigDecimal monto);
}
