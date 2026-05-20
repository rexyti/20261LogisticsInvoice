package com.logistica.domain.registrarEstadoPago.models;

import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;

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
        Long ultimaSecuenciaProcesada,
        String numeroVoucher,
        Instant fechaProcesamiento
) {
    public RegistrarEstadoPagoPago actualizarEstado(RegistrarEstadoPagoEstadoPagoEnum nuevoEstado,
                                                     Instant fechaActualizacion, Long secuencia) {
        return new RegistrarEstadoPagoPago(idPago, idUsuario, montoBase, fecha, idPenalidad, montoNeto,
                idLiquidacion, nuevoEstado, fechaActualizacion, secuencia, numeroVoucher, fechaProcesamiento);
    }

    public RegistrarEstadoPagoPago conVoucher(String voucher, Instant fechaProc) {
        return new RegistrarEstadoPagoPago(idPago, idUsuario, montoBase, fecha, idPenalidad, montoNeto,
                idLiquidacion, estadoActual, fechaUltimaActualizacion, ultimaSecuenciaProcesada, voucher, fechaProc);
    }
}
