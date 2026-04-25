package com.logistica.domain.models;

import com.logistica.domain.enums.EstadoPaquete;

import java.util.UUID;

public record Paquete(
        UUID idPaquete,
        UUID idRuta,
        EstadoPaquete estadoActual,
        Long version
) {
    public Paquete(UUID idPaquete, UUID idRuta, EstadoPaquete estadoActual) {
        this(idPaquete, idRuta, estadoActual, null);
    }

    public Paquete actualizarEstado(EstadoPaquete nuevoEstado) {
        return new Paquete(idPaquete, idRuta, nuevoEstado, version);
    }

    public boolean tieneMismoEstado(EstadoPaquete nuevoEstado) {
        return estadoActual != null && estadoActual == nuevoEstado;
    }
}