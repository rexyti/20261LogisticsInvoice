package com.logistica.RegistrarEstadoPago.domain.models;

import com.logistica.RegistrarEstadoPago.domain.enums.RegistrarEstadoPagoEstadoPagoEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RegistrarEstadoPagoPago(
        UUID idPago,
        UUID idUsuario,
        BigDecimal montoBase,
        Instant fecha,
        UUID idPenalidad,
        BigDecimal montoNeto,
        UUID idLiquidacion,
        RegistrarEstadoPagoEstadoPagoEnum estadoActual,
        Instant fechaUltimaActualizacion,
        Long ultimaSecuenciaProcesada
) {
    public RegistrarEstadoPagoPago actualizarEstado(RegistrarEstadoPagoEstadoPagoEnum nuevoEstado, Instant fechaActualizacion, Long secuencia) {
        return new RegistrarEstadoPagoPago(idPago, idUsuario, montoBase, fecha, idPenalidad, montoNeto,
                idLiquidacion, nuevoEstado, fechaActualizacion, secuencia);
    }
}
