package com.logistica.domain.cierreRuta.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum MotivoFalla {

    DIRECCION_ERRONEA(
            "DIRECCION_INCORRECTA",
            ResponsableFalla.CLIENTE,
            0.30,
            0.50,
            false
    ),

    CLIENTE_AUSENTE(
            "CLIENTE_AUSENTE",
            ResponsableFalla.CLIENTE,
            0.30,
            0.50,
            false
    ),

    RECHAZADO(
            "RECHAZADO_POR_CLIENTE",
            ResponsableFalla.CLIENTE,
            0.30,
            0.50,
            false
    ),

    PAQUETE_DANADO(
            "DAÑADO_EN_RUTA",
            ResponsableFalla.TRANSPORTISTA,
            0.0,
            0.0,
            false
    ),

    PERDIDA_PAQUETE(
            "EXTRAVIADO",
            ResponsableFalla.TRANSPORTISTA,
            0.0,
            0.0,
            false
    ),

    DEVOLUCION(
            "DEVOLUCION",
            ResponsableFalla.EMPRESA,
            0.0,
            0.0,
            false
    ),

    ZONA_DIFICIL_ACCESO(
            "ZONA_DIFICIL_ACCESO",
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

    MotivoFalla(
            String valorJson,
            ResponsableFalla responsable,
            double porcentajeMinimo,
            double porcentajeMaximo,
            boolean porcentajePendiente
    ) {
        this.valorJson = valorJson;
        this.responsable = responsable;
        this.porcentajeMinimo = porcentajeMinimo;
        this.porcentajeMaximo = porcentajeMaximo;
        this.porcentajePendiente = porcentajePendiente;
    }

    @JsonCreator
    public static MotivoFalla fromValue(String valor) {

        if (valor == null || valor.isBlank()) {
            return null;
        }

        String normalizado = valor.trim().toUpperCase();

        for (MotivoFalla m : values()) {

            if (m.valorJson.equalsIgnoreCase(normalizado)
                    || m.name().equalsIgnoreCase(normalizado)) {

                return m;
            }
        }

        throw new IllegalArgumentException(
                "Motivo de falla desconocido: " + valor);
    }
}