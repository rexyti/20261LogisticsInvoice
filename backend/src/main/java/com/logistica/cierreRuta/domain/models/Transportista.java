package com.logistica.cierreRuta.domain.models;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class Transportista {

    private UUID transportistaId;
    private String nombre;

    public Transportista(UUID transportistaId, String nombre) {

        if (transportistaId == null) {
            throw new IllegalArgumentException("transportistaId es obligatorio");
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("nombre del transportista es obligatorio");
        }

        this.transportistaId = transportistaId;
        this.nombre = nombre.trim();
    }
}
