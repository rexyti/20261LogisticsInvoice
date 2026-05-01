package com.logistica.RegistrarEstadoPago.application.dtos.response;

import com.logistica.RegistrarEstadoPago.domain.enums.EstadoEventoTransaccion;
import com.logistica.RegistrarEstadoPago.domain.enums.RegistrarEstadoPagoEstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

public record EventoTransaccionResponseDTO(
        UUID idEvento,
        String idTransaccionBanco,
        UUID idPago,
        UUID idLiquidacion,
        RegistrarEstadoPagoEstadoPagoEnum estadoSolicitado,
        EstadoEventoTransaccion estadoProcesamiento,
        Instant fechaRecepcion,
        Instant fechaEventoBanco,
        Long secuencia,
        String mensajeError
) {}
