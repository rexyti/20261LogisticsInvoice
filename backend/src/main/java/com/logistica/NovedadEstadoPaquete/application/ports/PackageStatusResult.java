package com.logistica.NovedadEstadoPaquete.application.ports;

public record PackageStatusResult(
        int codigoRespuestaHTTP,
        String jsonRecibido,
        String estadoRaw,
        boolean exitoso,
        boolean paqueteNoEncontrado,
        boolean pendientePorSincronizacion
) {

    public static PackageStatusResult exitoso(int codigo, String json, String estado) {
        return new PackageStatusResult(codigo, json, estado, true, false, false);
    }

    public static PackageStatusResult noEncontrado(int codigo, String json) {
        return new PackageStatusResult(codigo, json, null, false, true, false);
    }

    public static PackageStatusResult error(int codigo, String json) {
        return new PackageStatusResult(codigo, json, null, false, false, false);
    }

    public static PackageStatusResult pendiente(String motivo) {
        return new PackageStatusResult(-1, motivo, null, false, false, true);
    }
}
