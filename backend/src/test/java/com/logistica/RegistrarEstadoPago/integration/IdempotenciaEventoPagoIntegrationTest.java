package com.logistica.RegistrarEstadoPago.integration;

import com.logistica.RegistrarEstadoPago.domain.enums.EstadoEventoTransaccion;
import com.logistica.RegistrarEstadoPago.domain.enums.EstadoPagoEnum;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.entities.EstadoPagoEntity;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.entities.EventoTransaccionEntity;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class IdempotenciaEventoPagoIntegrationTest {

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
    void mismoidTransaccionBanco_noGeneraRegistrosDuplicados() throws InterruptedException {
        Map<String, Object> body = Map.of(
                "idEvento", "evt-idem-001",
                "idTransaccionBanco", "txn-idem-unico",
                "idPago", idPago.toString(),
                "idLiquidacion", idLiquidacion.toString(),
                "estado", "EN_PROCESO",
                "fechaEvento", "2026-04-26T10:30:00",
                "secuencia", 1
        );

        // Primera llamada — procesa el evento
        restTemplate.exchange("/api/v1/pagos/webhook/estado",
                HttpMethod.POST, new HttpEntity<>(body, jsonHeaders()), Map.class);
        Thread.sleep(1000);

        int estadosAntesDelDuplicado = estadoPagoJpaRepository
                .findByIdPagoOrderByFechaRegistroDesc(idPago).size();
        int eventosAntesDuplicado = eventoTransaccionJpaRepository
                .findByIdPagoOrderByFechaRecepcionAsc(idPago).size();

        assertThat(estadosAntesDelDuplicado).isEqualTo(1);

        // Segunda llamada — mismo idTransaccionBanco (duplicado)
        ResponseEntity<Map> segundaRespuesta = restTemplate.exchange(
                "/api/v1/pagos/webhook/estado",
                HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()),
                Map.class
        );
        assertThat(segundaRespuesta.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        Thread.sleep(1000);

        // No debe crearse un nuevo EstadoPago
        List<EstadoPagoEntity> estadosDespues = estadoPagoJpaRepository
                .findByIdPagoOrderByFechaRegistroDesc(idPago);
        assertThat(estadosDespues).hasSize(estadosAntesDelDuplicado);

        // No debe crearse un nuevo EventoTransaccion (duplicado ignorado silenciosamente)
        List<EventoTransaccionEntity> eventosDespues = eventoTransaccionJpaRepository
                .findByIdPagoOrderByFechaRecepcionAsc(idPago);
        assertThat(eventosDespues).hasSize(eventosAntesDuplicado);

        // El pago debe mantenerse en EN_PROCESO
        PagoEntity pago = pagoJpaRepository.findById(idPago).orElseThrow();
        assertThat(pago.getEstadoActual()).isEqualTo(EstadoPagoEnum.EN_PROCESO);

        // El evento original debe estar marcado como PROCESADO
        assertThat(eventosDespues.get(0).getEstadoProcesamiento())
                .isEqualTo(EstadoEventoTransaccion.PROCESADO);
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
