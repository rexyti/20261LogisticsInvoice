package com.logistica.domain.cierreRuta.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum MotivoFalla {

    DIRECCION_ERRONEA("DIRECCIÓN_ERRONEA", ResponsableFalla.CLIENTE, 0.30, 0.50, false),
    CLIENTE_AUSENTE("CLIENTE_AUSENTE", ResponsableFalla.CLIENTE, 0.30, 0.50, false),
    RECHAZADO("RECHAZADO", ResponsableFalla.CLIENTE, 0.30, 0.50, false),
    PAQUETE_DANADO("PAQUETE_DAÑADO", ResponsableFalla.TRANSPORTISTA, 0.0, 0.0, false),
    PERDIDA_PAQUETE("PÉRDIDA_PAQUETE", ResponsableFalla.TRANSPORTISTA, 0.0, 0.0, false),

    ZONA_DIFICIL_ACCESO(
            "ZONA DE DIFÍCIL ACCESO / ORDEN PÚBLICO",
            ResponsableFalla.EMPRESA,
            0.0,
            0.0,
            true
    );

    private final String valorJson;
    private final ResponsableFalla responsable;
    private final double porcentajeMinimo;
    private final double porcentajeMaximo;
    private final boolean porcentajePendiente;

    MotivoFalla(String valorJson, ResponsableFalla responsable, double porcentajeMinimo, double porcentajeMaximo, boolean porcentajePendiente) {
        this.valorJson = valorJson;
        this.responsable = responsable;
        this.porcentajeMinimo = porcentajeMinimo;
        this.porcentajeMaximo = porcentajeMaximo;
        this.porcentajePendiente = porcentajePendiente;
    }

    @JsonCreator
    public static MotivoFalla fromValue(String valor) {
        if (valor == null) return null;
        for (MotivoFalla m : values()) {
            if (m.valorJson.equalsIgnoreCase(valor) || m.name().equalsIgnoreCase(valor)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Motivo de falla desconocido: " + valor);
    }
}