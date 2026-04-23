package com.logistica.domain.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class RutaCerradaProcesadaEvent {
    private final UUID rutaId;
    private final String tipoAlerta;
    private final String detalle;
}
