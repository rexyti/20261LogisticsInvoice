package com.logistica.domain.enums;

public enum TipoVehiculo {
    MOTO,
    VAN,
    NHR,
    TURBO;

    public static boolean isKnown(String tipo) {
        if (tipo == null) return false;
        for (TipoVehiculo t : values()) {
            if (t.name().equalsIgnoreCase(tipo)) return true;
        }
        return false;
    }
}
