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

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ActualizacionEstadoPagoIntegrationTest {

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

        pagoJpaRepository.save(RegistrarEstadoPagoPagoEntity.builder()
                .idPago(idPago)
                .idLiquidacion(idLiquidacion)
                .estadoActual(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO)
                .fechaUltimaActualizacion(Instant.now())
                .ultimaSecuenciaProcesada(1L)
                .build());
    }

    @Test
    void actualizacion_EN_PROCESO_a_PAGADO_exitosa() throws InterruptedException {
        Map<String, Object> body = Map.of(
                "id_evento", "evt-act-001",
                "id_transaccion_banco", "txn-act-001",
                "id_pago", idPago.toString(),
                "id_liquidacion", idLiquidacion.toString(),
                "estado", "PAGADO",
                "fecha_evento", "2026-04-26T10:35:00",
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

        RegistrarEstadoPagoPagoEntity pago = pagoJpaRepository.findById(idPago).orElseThrow();
        assertThat(pago.getEstadoActual()).isEqualTo(RegistrarEstadoPagoEstadoPagoEnum.PAGADO);
        assertThat(pago.getUltimaSecuenciaProcesada()).isEqualTo(2L);
        assertThat(estadoPagoJpaRepository.findByIdPagoOrderByFechaRegistroDesc(idPago)).isNotEmpty();
    }

    @Test
    void actualizacion_EN_PROCESO_a_RECHAZADO_exitosa() throws InterruptedException {
        Map<String, Object> body = Map.of(
                "id_evento", "evt-act-002",
                "id_transaccion_banco", "txn-act-002",
                "id_pago", idPago.toString(),
                "id_liquidacion", idLiquidacion.toString(),
                "estado", "RECHAZADO",
                "fecha_evento", "2026-04-26T10:40:00",
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

        RegistrarEstadoPagoPagoEntity pago = pagoJpaRepository.findById(idPago).orElseThrow();
        assertThat(pago.getEstadoActual()).isEqualTo(RegistrarEstadoPagoEstadoPagoEnum.RECHAZADO);
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
