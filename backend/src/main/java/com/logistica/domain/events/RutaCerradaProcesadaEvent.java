package com.logistica.domain.events;

import com.logistica.domain.enums.EstadoProcesamiento;
import com.logistica.domain.enums.TipoAlertaRuta;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class RutaCerradaProcesadaEvent {

    private final UUID rutaId;


    private final EstadoProcesamiento estadoProcesamiento;


    private final TipoAlertaRuta tipoAlerta;

    private final String detalle;

    private final LocalDateTime ocurridoEn;
}