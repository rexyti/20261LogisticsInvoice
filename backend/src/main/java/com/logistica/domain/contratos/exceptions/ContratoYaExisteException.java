package com.logistica.domain.contratos.exceptions;

import com.logistica.domain.shared.exceptions.DomainException;

public class ContratoYaExisteException extends RuntimeException {
    public ContratoYaExisteException(String idContrato) {
        super("El contrato con este identificador ya existe: " + idContrato);
    }
}
