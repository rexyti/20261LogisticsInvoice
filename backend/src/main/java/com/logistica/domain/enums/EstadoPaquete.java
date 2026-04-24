package com.logistica.domain.enums;

import java.util.Arrays;
import java.util.Optional;

public enum EstadoPaquete {

    ENTREGADO(100),
    DEVUELTO(50),
    DANADO(0),
    EXTRAVIADO(0);

    private final int porcentajePago;

    EstadoPaquete(int porcentajePago) {
        this.porcentajePago = porcentajePago;
    }

    public int getPorcentajePago() {
        return porcentajePago;
    }

    /**
     * Maps raw API string to enum. Handles "DAÑADO" → DANADO alias.
     */
    public static Optional<EstadoPaquete> fromString(String estado) {
        if (estado == null) return Optional.empty();
        String upper = estado.trim().toUpperCase();
        // Alias for special character variant received from external API
        if ("DAÑADO".equals(upper)) return Optional.of(DANADO);
        return Arrays.stream(values())
                .filter(e -> e.name().equals(upper))
                .findFirst();
    }
}
