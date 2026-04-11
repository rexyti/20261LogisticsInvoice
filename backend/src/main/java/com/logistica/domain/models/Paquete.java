package com.logistica.domain.models;

import java.util.UUID;

public class Paquete {
    private UUID id;
    private String estadoFinal; // Ej: "ENTREGADO", "FALLIDO_CLIENTE", "FALLIDO_TRANSPORTISTA"
    private String novedades;

    // Constructor, getters, and setters

    public Paquete(UUID id, String estadoFinal, String novedades) {
        this.id = id;
        this.estadoFinal = estadoFinal;
        this.novedades = novedades;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEstadoFinal() {
        return estadoFinal;
    }

    public void setEstadoFinal(String estadoFinal) {
        this.estadoFinal = estadoFinal;
    }

    public String getNovedades() {
        return novedades;
    }

    public void setNovedades(String novedades) {
        this.novedades = novedades;
    }
}
