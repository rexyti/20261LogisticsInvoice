package com.logistica.domain.registrarEstadoPago.models;

import com.logistica.domain.registrarEstadoPago.enums.EstadoEventoTransaccion;
import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

public record RegistrarEstadoPagoEventoTransaccion(
        UUID idEvento,
        String idTransaccionBanco,
        UUID idPago,
        UUID idLiquidacion,
        String payloadRecibido,
        Instant fechaRecepcion,
        Instant fechaEventoBanco,
        Long secuencia,
        RegistrarEstadoPagoEstadoPagoEnum estadoSolicitado,
        EstadoEventoTransaccion estadoProcesamiento,
        String mensajeError,
        boolean procesado
) {
    public RegistrarEstadoPagoEventoTransaccion conEstadoProcesamiento(EstadoEventoTransaccion nuevoEstado, String error) {
        return new RegistrarEstadoPagoEventoTransaccion(idEvento, idTransaccionBanco, idPago, idLiquidacion,
                payloadRecibido, fechaRecepcion, fechaEventoBanco, secuencia,
                estadoSolicitado, nuevoEstado, error, true);
    }
}
