package com.logistica.NovedadEstadoPaquete.application.dtos.response;

public record SincronizacionResultadoDTO(
        Long    idPaquete,
        String  estadoActual,
        Integer porcentajePago,
        String  resultado,
        String  mensaje
) {
    public static SincronizacionResultadoDTO exitoso(Long idPaquete, String estado, int porcentaje) {
        return new SincronizacionResultadoDTO(idPaquete, estado, porcentaje, "EXITOSO", null);
    }

    public static SincronizacionResultadoDTO noEncontrado(Long idPaquete) {
        return new SincronizacionResultadoDTO(idPaquete, null, null,
                "PAQUETE_NO_ENCONTRADO", "NovedadEstadoPaquetePaquete no encontrado en el módulo de gestión");
    }

    public static SincronizacionResultadoDTO estadoNoMapeado(Long idPaquete, String estadoRaw) {
        return new SincronizacionResultadoDTO(idPaquete, estadoRaw, null,
                "ESTADO_NO_MAPEADO", "Estado recibido sin regla de pago: " + estadoRaw);
    }

    public static SincronizacionResultadoDTO pendiente(Long idPaquete) {
        return new SincronizacionResultadoDTO(idPaquete, "PENDIENTE_SINCRONIZACION", null,
                "PENDIENTE", "Sincronización pendiente por fallo de comunicación");
    }

    public static SincronizacionResultadoDTO error(Long idPaquete, int codigo) {
        return new SincronizacionResultadoDTO(idPaquete, null, null,
                "ERROR_HTTP", "Respuesta HTTP " + codigo + " del módulo de gestión");
    }
}
