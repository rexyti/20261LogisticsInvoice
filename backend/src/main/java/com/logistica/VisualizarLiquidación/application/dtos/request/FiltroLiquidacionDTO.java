package com.logistica.VisualizarLiquidación.application.dtos.request;

import jakarta.validation.constraints.AssertTrue;

import java.util.UUID;

public class FiltroLiquidacionDTO {

    private UUID idLiquidacion;
    private UUID idRuta;

    public UUID getIdLiquidacion() {
        return idLiquidacion;
    }

    public void setIdLiquidacion(UUID idLiquidacion) {
        this.idLiquidacion = idLiquidacion;
    }

    public UUID getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(UUID idRuta) {
        this.idRuta = idRuta;
    }

    @AssertTrue(message = "Debe proporcionar al menos un criterio de busqueda: idLiquidacion o idRuta")
    public boolean isTieneCriterio() {
        return idLiquidacion != null || idRuta != null;
    }
}
