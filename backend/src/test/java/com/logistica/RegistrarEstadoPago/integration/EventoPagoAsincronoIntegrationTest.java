package com.logistica.RegistrarEstadoPago.integration;

import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;
import com.logistica.infrastructure.registrarEstadoPago.persistence.entities.LiquidacionReferenciaEntity;
import com.logistica.infrastructure.registrarEstadoPago.persistence.entities.RegistrarEstadoPagoPagoEntity;
import com.logistica.infrastructure.registrarEstadoPago.persistence.repositories.EventoTransaccionJpaRepository;
import com.logistica.infrastructure.registrarEstadoPago.persistence.repositories.RegistrarEstadoPagoEstadoPagoJpaRepository;
import com.logistica.infrastructure.registrarEstadoPago.persistence.repositories.RegistrarEstadoPagoLiquidacionJpaRepository;
import com.logistica.infrastructure.registrarEstadoPago.persistence.repositories.RegistrarEstadoPagoPagoJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EventoPagoAsincronoIntegrationTest {

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private RegistrarEstadoPagoLiquidacionJpaRepository liquidacionJpaRepository;
    @Autowired private RegistrarEstadoPagoPagoJpaRepository pagoJpaRepository;
    @Autowired private RegistrarEstadoPagoEstadoPagoJpaRepository estadoPagoJpaRepository;
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
    void webhook_responde202_antesDeFinalizarProcesamiento() throws InterruptedException {
        Map<String, Object> body = Map.of(
                "id_evento", "evt-async-001",
                "id_transaccion_banco", "txn-async-001",
                "id_pago", idPago.toString(),
                "id_liquidacion", idLiquidacion.toString(),
                "estado", "EN_PROCESO",
                "fecha_evento", "2026-04-26T10:30:00",
                "secuencia", 1
        );

        long inicio = System.currentTimeMillis();
        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/pagos/webhook/estado",
                HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()),
                Map.class
        );
        long duracion = System.currentTimeMillis() - inicio;

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody().get("procesamiento")).isEqualTo("ASINCRONO");

        Thread.sleep(1500);

        RegistrarEstadoPagoPagoEntity pago = pagoJpaRepository.findById(idPago).orElse(null);
        assertThat(pago).isNotNull();
        assertThat(pago.getEstadoActual()).isEqualTo(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO);
        assertThat(estadoPagoJpaRepository.findByIdPagoOrderByFechaRegistroDesc(idPago)).isNotEmpty();
    }

    @Test
    void webhook_liquidacionInexistente_responde202_registraError() throws InterruptedException {
        UUID liquidacionFalsa = UUID.fromString("00000000-0000-0000-0000-000000000000");
        Map<String, Object> body = Map.of(
                "id_evento", "evt-async-002",
                "id_transaccion_banco", "txn-async-002",
                "id_pago", idPago.toString(),
                "id_liquidacion", liquidacionFalsa.toString(),
                "estado", "EN_PROCESO",
                "fecha_evento", "2026-04-26T10:45:00",
                "secuencia", 1
        );

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/pagos/webhook/estado",
                HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        Thread.sleep(1000);

        assertThat(pagoJpaRepository.findById(idPago)).isEmpty();
        assertThat(estadoPagoJpaRepository.findByIdPagoOrderByFechaRegistroDesc(idPago)).isEmpty();
    }

    @Test
    void webhook_estadoDesconocido_retorna400() throws Exception {
        Map<String, Object> body = Map.of(
                "id_evento", "evt-async-003",
                "id_transaccion_banco", "txn-async-003",
                "id_pago", idPago.toString(),
                "id_liquidacion", idLiquidacion.toString(),
                "estado", "APROBADO_PARCIALMENTE",
                "fecha_evento", "2026-04-26T10:50:00",
                "secuencia", 1
        );

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/pagos/webhook/estado",
                HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
