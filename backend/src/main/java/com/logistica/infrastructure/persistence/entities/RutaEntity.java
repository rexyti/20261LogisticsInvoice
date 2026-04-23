package com.logistica.infrastructure.persistence.entities;

import com.logistica.domain.enums.EstadoProcesamiento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ruta")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RutaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ruta_id", nullable = false, unique = true)
    private UUID rutaId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "transportista_id", nullable = false)
    private TransportistaEntity transportista;

    @Column(name = "tipo_vehiculo")
    private String tipoVehiculo;

    @Column(name = "modelo_contrato")
    private String modeloContrato;

    @Column(name = "fecha_inicio_transito", nullable = false)
    private LocalDateTime fechaInicioTransito;

    @Column(name = "fecha_cierre", nullable = false)
    private LocalDateTime fechaCierre;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_procesamiento", nullable = false)
    private EstadoProcesamiento estadoProcesamiento;

    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<ParadaEntity> paradas = new ArrayList<>();

    public  void addParada(ParadaEntity parada){
        parada.setRuta(this);
        this.paradas.add(parada);
    }
}
