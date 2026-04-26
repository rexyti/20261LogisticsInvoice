package com.logistica.integration;

import com.logistica.domain.enums.EstadoEventoTransaccion;
import com.logistica.domain.enums.EstadoPagoEnum;
import com.logistica.infrastructure.persistence.entities.EventoTransaccionEntity;
import com.logistica.infrastructure.persistence.entities.LiquidacionReferenciaEntity;
import com.logistica.infrastructure.persistence.entities.PagoEntity;
import com.logistica.infrastructure.persistence.repositories.EstadoPagoJpaRepository;
import com.logistica.infrastructure.persistence.repositories.EventoTransaccionJpaRepository;
import com.logistica.infrastructure.persistence.repositories.LiquidacionJpaRepository;
import com.logistica.infrastructure.persistence.repositories.PagoJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RegistroEstadoPagoIntegrationTest {

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private LiquidacionJpaRepository liquidacionJpaRepository;
    @Autowired private PagoJpaRepository pagoJpaRepository;
    @Autowired private EstadoPagoJpaRepository estadoPagoJpaRepository;
    @Autowired private EventoTransaccionJpaRepository eventoTransaccionJpaRepository;

    private UUID idLiquidacion;
    private UUID idPago;

    @BeforeEach
    void setUp() {
        eventoTransaccionJpaRepository.deleteAll();
        estadoPagoJpaRepository.deleteAll();
        pagoJpaRepository.deleteAll();
        liquidacionJpaRepository.deleteAll();

        idLiquidacion = UUID.randomUUID();
        idPago = UUID.randomUUID();
        liquidacionJpaRepository.save(new LiquidacionReferenciaEntity(idLiquidacion));
    }

    @Test
    void registroInicial_flujoCompleto_persiste() throws InterruptedException {
        Map<String, Object> body = Map.of(
                "idEvento", "evt-reg-001",
                "idTransaccionBanco", "txn-reg-001",
                "idPago", idPago.toString(),
                "idLiquidacion", idLiquidacion.toString(),
                "estado", "EN_PROCESO",
                "fechaEvento", "2026-04-26T10:30:00",
                "secuencia", 1
        );

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/pagos/webhook/estado",
                HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).containsKey("mensaje");
        assertThat(response.getBody().get("procesamiento")).isEqualTo("ASINCRONO");

        Thread.sleep(1000);

        PagoEntity pago = pagoJpaRepository.findById(idPago).orElse(null);
        assertThat(pago).isNotNull();
        assertThat(pago.getEstadoActual()).isEqualTo(EstadoPagoEnum.EN_PROCESO);
        assertThat(pago.getIdLiquidacion()).isEqualTo(idLiquidacion);

        List<EventoTransaccionEntity> eventos = eventoTransaccionJpaRepository
                .findByIdPagoOrderByFechaRecepcionAsc(idPago);
        assertThat(eventos).isNotEmpty();
        assertThat(eventos.get(0).getEstadoProcesamiento()).isEqualTo(EstadoEventoTransaccion.PROCESADO);

        assertThat(estadoPagoJpaRepository.findByIdPagoOrderByFechaRegistroDesc(idPago)).isNotEmpty();
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
