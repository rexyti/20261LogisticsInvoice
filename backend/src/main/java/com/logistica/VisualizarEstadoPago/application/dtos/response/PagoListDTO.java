package com.logistica.VisualizarEstadoPago.application.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PagoListDTO {

    private final UUID pagoId;
    private final UUID liquidacionId;
    private final LocalDateTime fecha;
    private final BigDecimal monto;
    private final String estado;

    public PagoListDTO(UUID pagoId, UUID liquidacionId, LocalDateTime fecha,
                       BigDecimal monto, String estado) {
        this.pagoId = pagoId;
        this.liquidacionId = liquidacionId;
        this.fecha = fecha;
        this.monto = monto;
        this.estado = estado;
    }

    public UUID getPagoId() { return pagoId; }
    public UUID getLiquidacionId() { return liquidacionId; }
    public LocalDateTime getFecha() { return fecha; }
    public BigDecimal getMonto() { return monto; }
    public String getEstado() { return estado; }
}
