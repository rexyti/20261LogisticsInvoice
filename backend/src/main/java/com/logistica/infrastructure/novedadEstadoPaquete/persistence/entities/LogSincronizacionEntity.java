package com.logistica.infrastructure.novedadEstadoPaquete.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "log_sincronizacion",
       indexes = @Index(name = "idx_log_id_paquete", columnList = "id_paquete"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogSincronizacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_paquete", nullable = false)
    private Long idPaquete;

    @Column(name = "codigo_respuesta_http")
    private Integer codigoRespuestaHTTP;

    @Column(name = "json_recibido", columnDefinition = "TEXT")
    private String jsonRecibido;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
