package com.logistica.infrastructure.cierreRuta.web.handlers;

import com.logistica.domain.cierreRuta.events.RutaCerradaProcesadaEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Setter

@Component
public class RutaEventHandler {

    @EventListener
    public  void handle(RutaCerradaProcesadaEvent event){
        log.info("Evento recibido para ruta {} con estado {}",
                event.getRutaId(),
                event.getEstadoProcesamiento());
    }
}
