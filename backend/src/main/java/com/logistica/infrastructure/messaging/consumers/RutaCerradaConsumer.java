package com.logistica.infrastructure.messaging.consumers;

import com.logistica.application.cierreRuta.ports.in.ProcesarRutaCerradaPort;
import com.logistica.infrastructure.messaging.dtos.RutaCerradaMensajeDTO;
import com.logistica.infrastructure.messaging.mappers.RutaCerradaMensajeMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RutaCerradaConsumer {

    private final ProcesarRutaCerradaPort procesarRutaCerradaPort;
    private final RutaCerradaMensajeMapper mapper;

    @SqsListener("https://sqs.us-east-2.amazonaws.com/383941187903/logistics-cierre-ruta")
    public void consumir(RutaCerradaMensajeDTO mensaje, Acknowledgement ack) {
        log.info("Evento RUTA_CERRADA recibido para ruta_id: {}", mensaje.getRutaId());

        try {
            procesarRutaCerradaPort.ejecutar(mapper.toApplicationEvent(mensaje));
            ack.acknowledge();

        } catch (IllegalArgumentException e) {
            log.error("Error de validación no recuperable para ruta {}: {}",
                    mensaje.getRutaId(), e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("Error procesando ruta {}. Se reintentará.", mensaje.getRutaId(), e);
            throw e;
        }
    }
}
