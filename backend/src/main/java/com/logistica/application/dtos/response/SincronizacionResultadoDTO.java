package com.logistica.application.dtos.response;

public record SincronizacionResultadoDTO(
        String estado,
        Integer porcentajePago,
        String mensaje
) {
    public static SincronizacionResultadoDTO exitoso(int porcentajePago, String estado) {
        return new SincronizacionResultadoDTO(estado, porcentajePago, "Sincronización exitosa");
    }

    public static SincronizacionResultadoDTO pendiente(String idPaquete) {
        return new SincronizacionResultadoDTO("PENDIENTE_SINCRONIZACION", null,
                "Sincronización pendiente para paquete: " + idPaquete);
    }

    public static SincronizacionResultadoDTO noEncontrado(String idPaquete) {
        return new SincronizacionResultadoDTO("PAQUETE_NO_ENCONTRADO", null,
                "Paquete no encontrado: " + idPaquete);
    }

    public static SincronizacionResultadoDTO estadoNoMapeado(String estadoRecibido) {
        return new SincronizacionResultadoDTO("ESTADO_NO_MAPEADO", null,
                "Estado no reconocido por las reglas financieras: " + estadoRecibido);
    }
}
