package com.logistica.domain.enums;

public enum TipoAjuste {
    BONO,
    PENALIZACION;

    public boolean esBono() {
        return this == BONO;
    }

    public boolean esPenalizacion() {
        return this == PENALIZACION;
    }
}
