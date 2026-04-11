package com.logistica.domain.models;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class Liquidacion {

    private UUID id;
    private UUID idRuta;
    private UUID idContrato;
    private String estado;
    private BigDecimal valorFinal;
    private OffsetDateTime fechaCalculo;
    private List<Ajuste> ajustes;

    public Liquidacion(UUID id, UUID idRuta, UUID idContrato, String estado, BigDecimal valorFinal, OffsetDateTime fechaCalculalo, List<Ajuste> ajustes) {
        this.id = id;
        this.idRuta = idRuta;
        this.idContrato = idContrato;
        this.estado = estado;
        this.valorFinal = valorFinal;
        this.fechaCalculo = fechaCalculo;
        this.ajustes = ajustes;
    }

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

    public UUID getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(UUID idContrato) {
        this.idContrato = idContrato;
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

    public List<Ajuste> getAjustes() {
        return ajustes;
    }

    public void setAjustes(List<Ajuste> ajustes) {
        this.ajustes = ajustes;
    }
}
