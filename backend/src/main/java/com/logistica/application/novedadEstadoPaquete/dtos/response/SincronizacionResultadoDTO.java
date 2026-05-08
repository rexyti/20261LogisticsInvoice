package com.logistica.application.novedadEstadoPaquete.dtos.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SincronizacionResultadoDTO(
        UUID idPaquete,
        String  estadoActual,
        Integer porcentajePago,
        String  resultado,
        String  mensaje
) {
    public static SincronizacionResultadoDTO exitoso(UUID idPaquete, String estado, int porcentaje) {
        return new SincronizacionResultadoDTO(idPaquete, estado, porcentaje, "EXITOSO", null);
    }

    public static SincronizacionResultadoDTO noEncontrado(UUID idPaquete) {
        return new SincronizacionResultadoDTO(idPaquete, null, null,
                "PAQUETE_NO_ENCONTRADO", "NovedadEstadoPaquetePaquete no encontrado en el módulo de gestión");
    }

    public static SincronizacionResultadoDTO estadoNoMapeado(UUID idPaquete, String estadoRaw) {
        return new SincronizacionResultadoDTO(idPaquete, estadoRaw, null,
                "ESTADO_NO_MAPEADO", "Estado recibido sin regla de pago: " + estadoRaw);
    }

    public static SincronizacionResultadoDTO pendiente(UUID idPaquete) {
        return new SincronizacionResultadoDTO(idPaquete, "PENDIENTE_SINCRONIZACION", null,
                "PENDIENTE", "Sincronización pendiente por fallo de comunicación");
    }

    public static SincronizacionResultadoDTO error(UUID idPaquete, int codigo) {
        return new SincronizacionResultadoDTO(idPaquete, null, null,
                "ERROR_HTTP", "Respuesta HTTP " + codigo + " del módulo de gestión");
    }
}
