package com.logistica.RegistrarEstadoPago.domain.services;

import com.logistica.RegistrarEstadoPago.domain.enums.RegistrarEstadoPagoEstadoPagoEnum;
import com.logistica.RegistrarEstadoPago.exceptions.EstadoPagoInvalidoException;

import java.util.Set;

public class EstadoPagoDomainService {

    private static final Set<RegistrarEstadoPagoEstadoPagoEnum> ESTADOS_FINALES = Set.of(
            RegistrarEstadoPagoEstadoPagoEnum.PAGADO,
            RegistrarEstadoPagoEstadoPagoEnum.RECHAZADO
    );

    public boolean esEstadoFinal(RegistrarEstadoPagoEstadoPagoEnum estado) {
        return ESTADOS_FINALES.contains(estado);
    }

    public void validarEstadoConocido(RegistrarEstadoPagoEstadoPagoEnum estado) {
        if (estado == null) {
            throw new EstadoPagoInvalidoException("null");
        }
    }

    public boolean esEstadoValido(String nombreEstado) {
        try {
            RegistrarEstadoPagoEstadoPagoEnum.valueOf(nombreEstado);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
