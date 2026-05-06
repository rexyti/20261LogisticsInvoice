package com.logistica.application.registrarEstadoPago.dtos.response;

import com.logistica.domain.registrarEstadoPago.enums.EstadoEventoTransaccion;
import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;

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
