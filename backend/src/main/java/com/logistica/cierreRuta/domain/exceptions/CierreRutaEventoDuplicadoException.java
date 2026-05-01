package com.logistica.cierreRuta.domain.exceptions;

import java.util.UUID;

public class CierreRutaEventoDuplicadoException extends CierreRutaDomainException {
    private static final String MESSAGE = "Evento duplicado, ruta procesada: ";


    public CierreRutaEventoDuplicadoException(UUID rutaId) {
        super( MESSAGE + rutaId);
    }
}
