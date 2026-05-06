package com.logistica.domain.cierreRuta.events;

import com.logistica.domain.cierreRuta.enums.EstadoProcesamiento;
import com.logistica.domain.cierreRuta.enums.TipoAlertaRuta;
import com.logistica.domain.cierreRuta.ports.DomainEvent;
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