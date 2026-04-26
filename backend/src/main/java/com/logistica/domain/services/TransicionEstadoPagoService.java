package com.logistica.domain.services;

import com.logistica.domain.enums.EstadoPagoEnum;
import com.logistica.shared.exceptions.TransicionEstadoPagoInvalidaException;

import java.util.Map;
import java.util.Set;

public class TransicionEstadoPagoService {

    private static final Map<EstadoPagoEnum, Set<EstadoPagoEnum>> TRANSICIONES_VALIDAS = Map.of(
            EstadoPagoEnum.PENDIENTE, Set.of(EstadoPagoEnum.EN_PROCESO, EstadoPagoEnum.PAGADO, EstadoPagoEnum.RECHAZADO),
            EstadoPagoEnum.EN_PROCESO, Set.of(EstadoPagoEnum.PAGADO, EstadoPagoEnum.RECHAZADO)
    );

    public void validarTransicion(EstadoPagoEnum estadoActual, EstadoPagoEnum estadoNuevo) {
        Set<EstadoPagoEnum> permitidos = TRANSICIONES_VALIDAS.getOrDefault(estadoActual, Set.of());
        if (!permitidos.contains(estadoNuevo)) {
            throw new TransicionEstadoPagoInvalidaException(estadoActual.name(), estadoNuevo.name());
        }
    }

    public boolean esTransicionValida(EstadoPagoEnum estadoActual, EstadoPagoEnum estadoNuevo) {
        Set<EstadoPagoEnum> permitidos = TRANSICIONES_VALIDAS.getOrDefault(estadoActual, Set.of());
        return permitidos.contains(estadoNuevo);
    }
}
