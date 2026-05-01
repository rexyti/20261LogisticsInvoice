package com.logistica.RegistrarEstadoPago.unit;

import com.logistica.RegistrarEstadoPago.domain.enums.EstadoEventoTransaccion;
import com.logistica.RegistrarEstadoPago.domain.enums.RegistrarEstadoPagoEstadoPagoEnum;
import com.logistica.RegistrarEstadoPago.domain.models.RegistrarEstadoPagoEstadoPago;
import com.logistica.RegistrarEstadoPago.domain.models.RegistrarEstadoPagoEventoTransaccion;
import com.logistica.RegistrarEstadoPago.domain.models.LiquidacionReferencia;
import com.logistica.RegistrarEstadoPago.domain.models.RegistrarEstadoPagoPago;
import com.logistica.RegistrarEstadoPago.infrastructure.adapters.PagoMapper;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.entities.RegistrarEstadoPagoEstadoPagoEntity;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.entities.EventoTransaccionEntity;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.entities.LiquidacionReferenciaEntity;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.entities.RegistrarEstadoPagoPagoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PagoMapperTest {

    private PagoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PagoMapper();
    }

    @Test
    void pagoEntity_toDomain_mapeatodosLosCampos() {
        UUID idPago = UUID.randomUUID();
        UUID idLiquidacion = UUID.randomUUID();
        RegistrarEstadoPagoPagoEntity entity = RegistrarEstadoPagoPagoEntity.builder()
                .idPago(idPago)
                .idLiquidacion(idLiquidacion)
                .estadoActual(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO)
                .montoBase(new BigDecimal("1000.00"))
                .montoNeto(new BigDecimal("900.00"))
                .fechaUltimaActualizacion(Instant.now())
                .ultimaSecuenciaProcesada(1L)
                .build();

        RegistrarEstadoPagoPago domain = mapper.toDomain(entity);

        assertThat(domain.idPago()).isEqualTo(idPago);
        assertThat(domain.idLiquidacion()).isEqualTo(idLiquidacion);
        assertThat(domain.estadoActual()).isEqualTo(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO);
        assertThat(domain.montoBase()).isEqualByComparingTo(new BigDecimal("1000.00"));
    }

    @Test
    void pago_toEntity_mapeatodosLosCampos() {
        UUID idPago = UUID.randomUUID();
        UUID idLiquidacion = UUID.randomUUID();
        RegistrarEstadoPagoPago domain = new RegistrarEstadoPagoPago(idPago, null, new BigDecimal("500.00"), Instant.now(),
                null, new BigDecimal("450.00"), idLiquidacion,
                RegistrarEstadoPagoEstadoPagoEnum.PAGADO, Instant.now(), 2L);

        RegistrarEstadoPagoPagoEntity entity = mapper.toEntity(domain);

        assertThat(entity.getIdPago()).isEqualTo(idPago);
        assertThat(entity.getEstadoActual()).isEqualTo(RegistrarEstadoPagoEstadoPagoEnum.PAGADO);
        assertThat(entity.getUltimaSecuenciaProcesada()).isEqualTo(2L);
    }

    @Test
    void estadoPagoEntity_toDomain_mapeatodosLosCampos() {
        UUID idEstado = UUID.randomUUID();
        UUID idPago = UUID.randomUUID();
        RegistrarEstadoPagoEstadoPagoEntity entity = RegistrarEstadoPagoEstadoPagoEntity.builder()
                .idEstadoPago(idEstado)
                .idPago(idPago)
                .estado(RegistrarEstadoPagoEstadoPagoEnum.PAGADO)
                .fechaRegistro(Instant.now())
                .secuenciaEvento(2L)
                .build();

        RegistrarEstadoPagoEstadoPago domain = mapper.toDomain(entity);

        assertThat(domain.idEstadoPago()).isEqualTo(idEstado);
        assertThat(domain.idPago()).isEqualTo(idPago);
        assertThat(domain.estado()).isEqualTo(RegistrarEstadoPagoEstadoPagoEnum.PAGADO);
    }

    @Test
    void eventoTransaccionEntity_toDomain_mapeatodosLosCampos() {
        UUID idEvento = UUID.randomUUID();
        UUID idPago = UUID.randomUUID();
        EventoTransaccionEntity entity = EventoTransaccionEntity.builder()
                .idEvento(idEvento)
                .idTransaccionBanco("txn-001")
                .idPago(idPago)
                .idLiquidacion(UUID.randomUUID())
                .payloadRecibido("{\"banco\":\"test\"}")
                .fechaRecepcion(Instant.now())
                .fechaEventoBanco(Instant.now())
                .secuencia(1L)
                .estadoSolicitado(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO)
                .estadoProcesamiento(EstadoEventoTransaccion.PROCESADO)
                .procesado(true)
                .build();

        RegistrarEstadoPagoEventoTransaccion domain = mapper.toDomain(entity);

        assertThat(domain.idEvento()).isEqualTo(idEvento);
        assertThat(domain.idTransaccionBanco()).isEqualTo("txn-001");
        assertThat(domain.estadoProcesamiento()).isEqualTo(EstadoEventoTransaccion.PROCESADO);
        assertThat(domain.procesado()).isTrue();
    }

    @Test
    void liquidacionReferencia_mapeoRoundTrip() {
        UUID idLiquidacion = UUID.randomUUID();
        LiquidacionReferencia domain = new LiquidacionReferencia(idLiquidacion);

        LiquidacionReferenciaEntity entity = mapper.toEntity(domain);
        LiquidacionReferencia roundTrip = mapper.toDomain(entity);

        assertThat(roundTrip.idLiquidacion()).isEqualTo(idLiquidacion);
    }
}
