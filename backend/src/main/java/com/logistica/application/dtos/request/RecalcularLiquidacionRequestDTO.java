package com.logistica.application.dtos.request;

import java.util.List;

public class RecalcularLiquidacionRequestDTO {
    private List<AjusteDTO> ajustes;
    private String responsable;

    // Getters y Setters

    public List<AjusteDTO> getAjustes() {
        return ajustes;
    }

    public void setAjustes(List<AjusteDTO> ajustes) {
        this.ajustes = ajustes;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }
}
