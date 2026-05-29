package com.logistica.domain.conductor.models;

import com.logistica.domain.conductor.enums.EstadoConductor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class Conductor {

    private final UUID conductorId;
    private final String nombre;
    private final EstadoConductor estado;

    public boolean estaActivo() {
        return EstadoConductor.ACTIVO.equals(this.estado);
    }
}
