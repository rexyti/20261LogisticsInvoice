package com.logistica.RegistrarEstadoPago.domain.models;

import com.logistica.RegistrarEstadoPago.domain.enums.EstadoEventoTransaccion;
import com.logistica.RegistrarEstadoPago.domain.enums.RegistrarEstadoPagoEstadoPagoEnum;

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
