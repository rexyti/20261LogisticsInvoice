package com.logistica.cierreRuta.domain.enums;


import java.util.Arrays;

public enum CierreRutaTipoVehiculo {
    MOTO,
    VAN,
    NHR,
    TURBO;

    public static CierreRutaTipoVehiculo from(String tipo) {
        if (tipo == null) return null;

        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(tipo))
                .findFirst()
                .orElse(null); // o excepción si quieres fail-fast
    }
}
