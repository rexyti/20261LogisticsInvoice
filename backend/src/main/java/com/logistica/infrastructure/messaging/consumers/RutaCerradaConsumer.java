package com.logistica.infrastructure.messaging.consumers;

import com.fasterxml.jackson.databind.JsonNode;

import com.logistica.application.cierreRuta.ports.in.ProcesarRutaCerradaPort;
import com.logistica.infrastructure.messaging.dtos.RutaCerradaMensajeDTO;
import com.logistica.infrastructure.messaging.mappers.RutaCerradaMensajeMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class RutaCerradaConsumer {

    private final ProcesarRutaCerradaPort procesarRutaCerradaPort;
    private final RutaCerradaMensajeMapper mapper;

    @SqsListener("https://sqs.us-east-2.amazonaws.com/383941187903/logistics-cierre-ruta")
    public void consumir(JsonNode payload, Acknowledgement ack) {

        try {

            RutaCerradaMensajeDTO mensaje = JsonNodeMapper.fromJsonNode(payload, RutaCerradaMensajeDTO.class);

            procesarRutaCerradaPort.ejecutar(
                    mapper.toApplicationEvent(mensaje)
            );

            ack.acknowledge();

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}
