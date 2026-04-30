package com.logistica.RegistrarEstadoPago.domain.services;

import com.logistica.RegistrarEstadoPago.domain.enums.EstadoPagoEnum;
import com.logistica.RegistrarEstadoPago.exceptions.EstadoPagoInvalidoException;

import java.util.Set;

public class EstadoPagoDomainService {

    private static final Set<EstadoPagoEnum> ESTADOS_FINALES = Set.of(
            EstadoPagoEnum.PAGADO,
            EstadoPagoEnum.RECHAZADO
    );

    public boolean esEstadoFinal(EstadoPagoEnum estado) {
        return ESTADOS_FINALES.contains(estado);
    }

    public void validarEstadoConocido(EstadoPagoEnum estado) {
        if (estado == null) {
            throw new EstadoPagoInvalidoException("null");
        }
    }

    public boolean esEstadoValido(String nombreEstado) {
        try {
            EstadoPagoEnum.valueOf(nombreEstado);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
