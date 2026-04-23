package com.logistica.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MotivoFalla {

    DIRECCION_ERRONEA("DIRECCIÓN_ERRONEA", ResponsableFalla.CLIENTE, "30%-50%"),
    CLIENTE_AUSENTE("CLIENTE_AUSENTE", ResponsableFalla.CLIENTE, "30%-50%"),
    RECHAZADO("RECHAZADO", ResponsableFalla.CLIENTE, "30%-50%"),
    ZONA_DIFICIL_ACCESO("ZONA DE DIFÍCIL ACESSO / ORDEN PÚBLICO", ResponsableFalla.EMPRESA, "Por definir"),
    PAQUETE_DANADO("PAQUETE_DAÑADO", ResponsableFalla.TRANSPORTISTA, "0% + penalidad"),
    PERDIDA_PAQUETE("PÉRDIDA_PAQUETE", ResponsableFalla.TRANSPORTISTA, "0% + penalidad");

    private final String valorJson;
    private final ResponsableFalla responsable;
    private final String porcentajePago;

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
