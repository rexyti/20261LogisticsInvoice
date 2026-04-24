package com.logistica.domain.enums;

import java.util.Arrays;
import java.util.Optional;

public enum EstadoPaquete {
    ENTREGADO(100),
    DEVUELTO(50),
    DAÑADO(0),
    EXTRAVIADO(0),
    // Estado interno: la sincronización falló tras todos los reintentos
    PENDIENTE_SINCRONIZACION(0);

    private final int porcentajePago;

    EstadoPaquete(int porcentajePago) {
        this.porcentajePago = porcentajePago;
    }

    public int getPorcentajePago() {
        return porcentajePago;
    }

    /**
     * Mapea el string recibido del Módulo de Gestión a un EstadoPaquete financiero.
     * PENDIENTE_SINCRONIZACION es exclusivamente un estado interno y no se mapea desde el exterior.
     */
    public static Optional<EstadoPaquete> fromString(String estado) {
        if (estado == null) return Optional.empty();
        return Arrays.stream(values())
                .filter(e -> e != PENDIENTE_SINCRONIZACION)
                .filter(e -> e.name().equalsIgnoreCase(estado))
                .findFirst();
    }
}
