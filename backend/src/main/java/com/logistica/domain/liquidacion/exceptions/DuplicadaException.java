package com.logistica.domain.liquidacion.exceptions;

import org.springframework.http.HttpStatus;
import java.util.UUID;

public class DuplicadaException extends DomainException {

    private final UUID idRuta;

    public DuplicadaException(UUID idRuta) {
        super("Ya existe una liquidación para la ruta con id " + idRuta, HttpStatus.CONFLICT);
        this.idRuta = idRuta;
    }

    public UUID getIdRuta() {
        return idRuta;
    }
}