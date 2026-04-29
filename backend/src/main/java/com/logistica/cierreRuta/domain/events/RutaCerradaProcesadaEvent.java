package com.logistica.cierreRuta.domain.events;

import com.logistica.cierreRuta.domain.enums.EstadoProcesamiento;
import com.logistica.cierreRuta.domain.enums.TipoAlertaRuta;
import com.logistica.cierreRuta.domain.ports.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class RutaCerradaProcesadaEvent implements DomainEvent {

    private final UUID rutaId;


    private final EstadoProcesamiento estadoProcesamiento;


    private final TipoAlertaRuta tipoAlerta;

    private final String detalle;

    private final LocalDateTime ocurridoEn;

    @Override
    public LocalDateTime occurredOn() {
        return ocurridoEn;
    }
}