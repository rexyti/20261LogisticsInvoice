package com.logistica.infrastructure.persistence.entities;

import com.logistica.domain.enums.EstadoEventoTransaccion;
import com.logistica.domain.enums.EstadoPagoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "eventos_transaccion",
        uniqueConstraints = @UniqueConstraint(name = "uk_id_transaccion_banco", columnNames = {"id_transaccion_banco"}),
        indexes = {
                @Index(name = "idx_evento_id_pago", columnList = "id_pago"),
                @Index(name = "idx_evento_fecha_recepcion", columnList = "fecha_recepcion")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoTransaccionEntity {

    @Id
    @Column(name = "id_evento")
    private UUID idEvento;

    @Column(name = "id_transaccion_banco", nullable = false)
    private String idTransaccionBanco;

    @Column(name = "id_pago", nullable = false)
    private UUID idPago;

    @Column(name = "id_liquidacion", nullable = false)
    private UUID idLiquidacion;

    @Column(name = "payload_recibido", columnDefinition = "TEXT")
    private String payloadRecibido;

    @Column(name = "fecha_recepcion", nullable = false)
    private Instant fechaRecepcion;

    @Column(name = "fecha_evento_banco")
    private Instant fechaEventoBanco;

    @Column(name = "secuencia")
    private Long secuencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_solicitado")
    private EstadoPagoEnum estadoSolicitado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_procesamiento", nullable = false)
    private EstadoEventoTransaccion estadoProcesamiento;

    @Column(name = "mensaje_error", columnDefinition = "TEXT")
    private String mensajeError;

    @Column(name = "procesado", nullable = false)
    private boolean procesado;
}
