package com.logistica.contratos.domain.exceptions;



public class ContratoNotFoundException extends DomainException{
    public ContratoNotFoundException(String idContrato) {
        super("Contrato no encontrado con id: " + idContrato);
    }
}
