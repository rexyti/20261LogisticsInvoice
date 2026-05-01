package com.logistica.cierreRuta.infrastructure.web.handlers;

import com.logistica.cierreRuta.domain.events.RutaCerradaProcesadaEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Setter

@Component
public class CierreRutaRutaEventHandler {

    @EventListener
    public  void handle(RutaCerradaProcesadaEvent event){
        log.info("Evento recibido para ruta {} con estado {}",
                event.getRutaId(),
                event.getEstadoProcesamiento());
    }
}
