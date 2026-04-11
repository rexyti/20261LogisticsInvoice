package com.logistica.domain.models;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.List;

public class Ruta {
    private UUID id;
    private OffsetDateTime fechaInicio;
    private OffsetDateTime fechaCierre;
    private List<Paquete> paquetes;

    // Constructor, getters, and setters

    public Ruta(UUID id, OffsetDateTime fechaInicio, OffsetDateTime fechaCierre, List<Paquete> paquetes) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaCierre = fechaCierre;
        this.paquetes = paquetes;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public OffsetDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(OffsetDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public OffsetDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(OffsetDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public List<Paquete> getPaquetes() {
        return paquetes;
    }

    public void setPaquetes(List<Paquete> paquetes) {
        this.paquetes = paquetes;
    }
}
