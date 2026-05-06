package com.logistica.domain.liquidacion.exceptions;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class NotFoundException extends DomainException {

    private final UUID idLiquidacion;

    public NotFoundException(UUID idLiquidacion) {
        super("La liquidación con id " + idLiquidacion + " no existe" , HttpStatus.NOT_FOUND);
        this.idLiquidacion = idLiquidacion;
    }

    public UUID getIdLiquidacion() {
        return idLiquidacion;
    }
}
