package com.logistica.liquidacion.domain.exceptions;

import org.springframework.http.HttpStatus;
import java.util.UUID;

public class ContratoNotFoundException extends DomainException {

    private final UUID idContrato;

    public ContratoNotFoundException(UUID idContrato) {
        super("El contrato con id " + idContrato + " no existe", HttpStatus.NOT_FOUND);
        this.idContrato = idContrato;
    }

    public UUID getIdContrato() {
        return idContrato;
    }
}
