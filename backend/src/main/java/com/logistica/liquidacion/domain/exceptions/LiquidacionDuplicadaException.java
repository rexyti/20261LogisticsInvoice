package com.logistica.liquidacion.domain.exceptions;

import org.springframework.http.HttpStatus;
import java.util.UUID;

public class LiquidacionDuplicadaException extends DomainException {

    private final UUID idRuta;

    public LiquidacionDuplicadaException(UUID idRuta) {
        super("Ya existe una liquidación para la ruta con id " + idRuta, HttpStatus.CONFLICT);
        this.idRuta = idRuta;
    }

    public UUID getIdRuta() {
        return idRuta;
    }
}