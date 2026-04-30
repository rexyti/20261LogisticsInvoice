package com.logistica.RegistrarEstadoPago.domain.models;

import com.logistica.RegistrarEstadoPago.domain.enums.EstadoPagoEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Pago(
        UUID idPago,
        UUID idUsuario,
        BigDecimal montoBase,
        Instant fecha,
        UUID idPenalidad,
        BigDecimal montoNeto,
        UUID idLiquidacion,
        EstadoPagoEnum estadoActual,
        Instant fechaUltimaActualizacion,
        Long ultimaSecuenciaProcesada
) {
    public Pago actualizarEstado(EstadoPagoEnum nuevoEstado, Instant fechaActualizacion, Long secuencia) {
        return new Pago(idPago, idUsuario, montoBase, fecha, idPenalidad, montoNeto,
                idLiquidacion, nuevoEstado, fechaActualizacion, secuencia);
    }
}
