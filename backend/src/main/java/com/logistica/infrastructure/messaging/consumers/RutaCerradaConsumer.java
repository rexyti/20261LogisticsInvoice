package com.logistica.infrastructure.messaging.consumers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    @SqsListener("https://sqs.us-east-2.amazonaws.com/383941187903/logistics-cierre-ruta")
    public void consumir(JsonNode payload, Acknowledgement ack) {

        try {

            RutaCerradaMensajeDTO mensaje = JsonNodeMapper.fromJsonNode(payload, RutaCerradaMensajeDTO.class);

            log.info("Evento RUTA_CERRADA recibido para ruta_id: {}", mensaje.getRutaId());

            procesarRutaCerradaPort.ejecutar(
                    mapper.toApplicationEvent(mensaje)
            );

            ack.acknowledge();

        } catch (Exception e) {
            log.error("Error procesando mensaje", e);
            throw new RuntimeException(e);
        }
    }
}
