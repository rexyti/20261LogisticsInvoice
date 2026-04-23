package com.logistica.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum EstadoParada {
    EXITOSA,
    FALLIDA;

    @JsonCreator
    public static EstadoParada fromValue(String value) {
        return value == null ? null : valueOf(value.toUpperCase());
    }
}
