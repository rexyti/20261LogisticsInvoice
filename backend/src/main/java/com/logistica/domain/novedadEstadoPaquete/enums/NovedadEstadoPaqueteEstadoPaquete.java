package com.logistica.domain.novedadEstadoPaquete.enums;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Optional;

public enum NovedadEstadoPaqueteEstadoPaquete {

    ENTREGADO(100),
    DEVUELTO(50),
    DANADO(0),
    EXTRAVIADO(0);

    private final int porcentajePago;

    NovedadEstadoPaqueteEstadoPaquete(int porcentajePago) {
        this.porcentajePago = porcentajePago;
    }

    public int getPorcentajePago() {
        return porcentajePago;
    }

    /**
     * Maps raw API state strings to internal rules. The normalization tolerates
     * accents, spaces and hyphens: "DAÑADO", "danado" and "Dañado" map to DANADO.
     */
    public static Optional<NovedadEstadoPaqueteEstadoPaquete> fromString(String estado) {
        if (estado == null || estado.isBlank()) {
            return Optional.empty();
        }

        String normalized = Normalizer.normalize(estado.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('-', '_')
                .replace(' ', '_')
                .toUpperCase();

        return Arrays.stream(values())
                .filter(e -> e.name().equals(normalized))
                .findFirst();
    }
}
