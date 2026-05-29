package com.logistica.domain.registrarEstadoPago.ports;

import java.util.UUID;

public interface LiquidacionPagoPort {
    boolean existe(UUID idLiquidacion);
    void marcarComoPagada(UUID idLiquidacion);
}
