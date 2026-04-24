package com.logistica.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "logs_sincronizacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogSincronizacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID idPaquete;

    @Column(nullable = false)
    private int codigoRespuestaHTTP;

    @Column(columnDefinition = "TEXT")
    private String jsonRecibido;

    @Column(nullable = false)
    private Instant timestamp;
}
