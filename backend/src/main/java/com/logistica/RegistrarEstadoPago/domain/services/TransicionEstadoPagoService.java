package com.logistica.RegistrarEstadoPago.domain.services;

import com.logistica.RegistrarEstadoPago.domain.enums.RegistrarEstadoPagoEstadoPagoEnum;
import com.logistica.RegistrarEstadoPago.domain.exceptions.TransicionEstadoPagoInvalidaException;

import java.util.Map;
import java.util.Set;

public class TransicionEstadoPagoService {

    private static final Map<RegistrarEstadoPagoEstadoPagoEnum, Set<RegistrarEstadoPagoEstadoPagoEnum>> TRANSICIONES_VALIDAS = Map.of(
            RegistrarEstadoPagoEstadoPagoEnum.PENDIENTE, Set.of(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO, RegistrarEstadoPagoEstadoPagoEnum.PAGADO, RegistrarEstadoPagoEstadoPagoEnum.RECHAZADO),
            RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO, Set.of(RegistrarEstadoPagoEstadoPagoEnum.PAGADO, RegistrarEstadoPagoEstadoPagoEnum.RECHAZADO)
    );

    public void validarTransicion(RegistrarEstadoPagoEstadoPagoEnum estadoActual, RegistrarEstadoPagoEstadoPagoEnum estadoNuevo) {
        Set<RegistrarEstadoPagoEstadoPagoEnum> permitidos = TRANSICIONES_VALIDAS.getOrDefault(estadoActual, Set.of());
        if (!permitidos.contains(estadoNuevo)) {
            throw new TransicionEstadoPagoInvalidaException(estadoActual.name(), estadoNuevo.name());
        }
    }

    public boolean esTransicionValida(RegistrarEstadoPagoEstadoPagoEnum estadoActual, RegistrarEstadoPagoEstadoPagoEnum estadoNuevo) {
        Set<RegistrarEstadoPagoEstadoPagoEnum> permitidos = TRANSICIONES_VALIDAS.getOrDefault(estadoActual, Set.of());
        return permitidos.contains(estadoNuevo);
    }
}
