package com.logistica.contratos.domain.exceptions;

public class ContratoYaExisteException extends RuntimeException {
    public ContratoYaExisteException(String idContrato) {
        super("El contrato con este identificador ya existe: " + idContrato);
    }
}
