package com.logistica.domain.contratos.exceptions;

import com.logistica.domain.shared.exceptions.DomainException;



public class ContratoNotFoundException extends DomainException{
    public ContratoNotFoundException(String idContrato) {
        super("Contrato no encontrado con id: " + idContrato);
    }
}
