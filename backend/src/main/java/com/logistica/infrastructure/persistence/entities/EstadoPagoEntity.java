package com.logistica.infrastructure.persistence.entities;

import com.logistica.domain.enums.EstadoPagoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "estados_pago",
        indexes = {
                @Index(name = "idx_estado_pago_id_pago", columnList = "id_pago"),
                @Index(name = "idx_estado_pago_fecha", columnList = "fecha_registro")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadoPagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_estado_pago")
    private UUID idEstadoPago;

    @Column(name = "id_pago", nullable = false)
    private UUID idPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPagoEnum estado;

    @Column(name = "fecha_registro", nullable = false)
    private Instant fechaRegistro;

    @Column(name = "fecha_evento_banco")
    private Instant fechaEventoBanco;

    @Column(name = "secuencia_evento")
    private Long secuenciaEvento;

    @Column(name = "id_evento_transaccion")
    private UUID idEventoTransaccion;
}
