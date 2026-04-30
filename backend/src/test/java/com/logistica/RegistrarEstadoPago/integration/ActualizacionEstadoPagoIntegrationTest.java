package com.logistica.RegistrarEstadoPago.integration;

import com.logistica.RegistrarEstadoPago.domain.enums.EstadoPagoEnum;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.entities.LiquidacionReferenciaEntity;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.entities.PagoEntity;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.repositories.EstadoPagoJpaRepository;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.repositories.EventoTransaccionJpaRepository;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.repositories.LiquidacionJpaRepository;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.repositories.PagoJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ActualizacionEstadoPagoIntegrationTest {

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

        pagoJpaRepository.save(PagoEntity.builder()
                .idPago(idPago)
                .idLiquidacion(idLiquidacion)
                .estadoActual(EstadoPagoEnum.EN_PROCESO)
                .fechaUltimaActualizacion(Instant.now())
                .ultimaSecuenciaProcesada(1L)
                .build());
    }

    @Test
    void actualizacion_EN_PROCESO_a_PAGADO_exitosa() throws InterruptedException {
        Map<String, Object> body = Map.of(
                "idEvento", "evt-act-001",
                "idTransaccionBanco", "txn-act-001",
                "idPago", idPago.toString(),
                "idLiquidacion", idLiquidacion.toString(),
                "estado", "PAGADO",
                "fechaEvento", "2026-04-26T10:35:00",
                "secuencia", 2
        );

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/pagos/webhook/estado",
                HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        Thread.sleep(1000);

        PagoEntity pago = pagoJpaRepository.findById(idPago).orElseThrow();
        assertThat(pago.getEstadoActual()).isEqualTo(EstadoPagoEnum.PAGADO);
        assertThat(pago.getUltimaSecuenciaProcesada()).isEqualTo(2L);
        assertThat(estadoPagoJpaRepository.findByIdPagoOrderByFechaRegistroDesc(idPago)).isNotEmpty();
    }

    @Test
    void actualizacion_EN_PROCESO_a_RECHAZADO_exitosa() throws InterruptedException {
        Map<String, Object> body = Map.of(
                "idEvento", "evt-act-002",
                "idTransaccionBanco", "txn-act-002",
                "idPago", idPago.toString(),
                "idLiquidacion", idLiquidacion.toString(),
                "estado", "RECHAZADO",
                "fechaEvento", "2026-04-26T10:40:00",
                "secuencia", 2
        );

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/pagos/webhook/estado",
                HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        Thread.sleep(1000);

        PagoEntity pago = pagoJpaRepository.findById(idPago).orElseThrow();
        assertThat(pago.getEstadoActual()).isEqualTo(EstadoPagoEnum.RECHAZADO);
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
