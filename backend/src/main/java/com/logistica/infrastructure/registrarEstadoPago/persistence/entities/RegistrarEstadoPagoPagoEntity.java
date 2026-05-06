package com.logistica.infrastructure.registrarEstadoPago.persistence.entities;

import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
@Entity
@Table(name = "pagos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarEstadoPagoPagoEntity {

    @Id
    @Column(name = "id_pago")
    private UUID idPago;

    @Column(name = "id_usuario")
    private UUID idUsuario;

    @Column(precision = 18, scale = 2)
    private BigDecimal montoBase;

    private Instant fecha;

    @Column(name = "id_penalidad")
    private UUID idPenalidad;

    @Column(precision = 18, scale = 2)
    private BigDecimal montoNeto;

    @Column(name = "id_liquidacion", nullable = false)
    private UUID idLiquidacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_actual", nullable = false)
    private RegistrarEstadoPagoEstadoPagoEnum estadoActual;

    @Column(name = "fecha_ultima_actualizacion", nullable = false)
    private Instant fechaUltimaActualizacion;

    @Column(name = "ultima_secuencia_procesada", nullable = false)
    private Long ultimaSecuenciaProcesada;

    @Version
    private Long version;
}
