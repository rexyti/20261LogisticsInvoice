package com.logistica.application.dtos.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class LiquidacionResponseDTO {
    private UUID id;
    private UUID idRuta;
    private String estado;
    private BigDecimal valorFinal;
    private OffsetDateTime fechaCalculo;
    private List<AjusteDTO> ajustes;

    // Getters y Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(UUID idRuta) {
        this.idRuta = idRuta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public OffsetDateTime getFechaCalculo() {
        return fechaCalculo;
    }

    public void setFechaCalculo(OffsetDateTime fechaCalculo) {
        this.fechaCalculo = fechaCalculo;
    }

    public List<AjusteDTO> getAjustes() {
        return ajustes;
    }

    public void setAjustes(List<AjusteDTO> ajustes) {
        this.ajustes = ajustes;
    }
}
