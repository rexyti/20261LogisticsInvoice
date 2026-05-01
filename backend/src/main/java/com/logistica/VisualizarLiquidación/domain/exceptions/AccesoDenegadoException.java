package com.logistica.VisualizarLiquidación.domain.exceptions;

public class AccesoDenegadoException extends RuntimeException {
    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
