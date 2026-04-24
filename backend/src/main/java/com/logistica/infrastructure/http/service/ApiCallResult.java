package com.logistica.infrastructure.http.service;

import lombok.Getter;

@Getter
public class ApiCallResult {

    private final int    codigoRespuestaHTTP;
    private final String jsonRecibido;
    private final String estadoRaw;
    private final boolean exitoso;
    private final boolean paqueteNoEncontrado;
    private final boolean pendientePorSincronizacion;

    private ApiCallResult(int codigo, String json, String estadoRaw,
                          boolean exitoso, boolean noEncontrado, boolean pendiente) {
        this.codigoRespuestaHTTP      = codigo;
        this.jsonRecibido             = json;
        this.estadoRaw                = estadoRaw;
        this.exitoso                  = exitoso;
        this.paqueteNoEncontrado      = noEncontrado;
        this.pendientePorSincronizacion = pendiente;
    }

    public static ApiCallResult exitoso(int codigo, String json, String estado) {
        return new ApiCallResult(codigo, json, estado, true, false, false);
    }

    public static ApiCallResult noEncontrado(int codigo, String json) {
        return new ApiCallResult(codigo, json, null, false, true, false);
    }

    public static ApiCallResult error(int codigo, String json) {
        return new ApiCallResult(codigo, json, null, false, false, false);
    }

    public static ApiCallResult pendiente(String motivo) {
        return new ApiCallResult(-1, motivo, null, false, false, true);
    }
}
