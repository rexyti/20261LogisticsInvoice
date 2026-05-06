package com.logistica.domain.liquidacion.exceptions;

import org.springframework.http.HttpStatus;
import java.util.UUID;

public class ContratoTarifaNoEncontradaException extends DomainException {

    private final UUID idContrato;

    public ContratoTarifaNoEncontradaException(UUID idContrato) {
        super("El contrato con id " + idContrato + " no existe", HttpStatus.NOT_FOUND);
        this.idContrato = idContrato;
    }

    public UUID getIdContrato() {
        return idContrato;
    }
}
