package com.logistica.domain.enums;


import java.util.Arrays;

public enum TipoVehiculo {
    MOTO,
    VAN,
    NHR,
    TURBO;

    public static boolean isKnown(String tipo) {
        if (tipo == null) return false;
        return Arrays.stream(values())
                .anyMatch(t -> t.name().equalsIgnoreCase(tipo));
    }
}
