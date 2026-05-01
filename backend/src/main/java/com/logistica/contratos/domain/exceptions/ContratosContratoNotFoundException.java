package com.logistica.contratos.domain.exceptions;



public class ContratosContratoNotFoundException extends DomainException{
    public ContratosContratoNotFoundException(String idContrato) {
        super("ContratosContrato no encontrado con id: " + idContrato);
    }
}
