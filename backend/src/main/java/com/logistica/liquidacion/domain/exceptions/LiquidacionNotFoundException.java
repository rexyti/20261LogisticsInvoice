package com.logistica.liquidacion.domain.exceptions;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class LiquidacionNotFoundException extends DomainException {

    private final UUID idLiquidacion;

    public LiquidacionNotFoundException(UUID idLiquidacion) {
        super("La liquidación con id " + idLiquidacion + " no existe" , HttpStatus.NOT_FOUND);
        this.idLiquidacion = idLiquidacion;
    }

    public UUID getIdLiquidacion() {
        return idLiquidacion;
    }
}
