package com.logistica.liquidacion.domain.exceptions;

import org.springframework.http.HttpStatus;
import java.util.UUID;

public class LiquidacionContratoNotFoundException extends LiquidacionDomainException {

    private final UUID idContrato;

    public LiquidacionContratoNotFoundException(UUID idContrato) {
        super("El contrato con id " + idContrato + " no existe", HttpStatus.NOT_FOUND);
        this.idContrato = idContrato;
    }

    public UUID getIdContrato() {
        return idContrato;
    }
}
