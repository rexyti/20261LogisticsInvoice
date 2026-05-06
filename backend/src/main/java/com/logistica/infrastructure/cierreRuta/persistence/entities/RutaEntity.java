package com.logistica.infrastructure.cierreRuta.persistence.entities;

import com.logistica.domain.cierreRuta.enums.EstadoProcesamiento;
import com.logistica.infrastructure.contratos.persistence.entities.TransportistaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "ruta",
        uniqueConstraints = @UniqueConstraint(name = "uk_ruta_ruta_id", columnNames = "ruta_id")
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RutaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;// Id tecnico

    @Column(name = "ruta_id", nullable = false, unique = true)
    private UUID rutaId; // Id de negocio

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transportista_id", nullable = false)
    private TransportistaEntity transportista;

    @Column(name = "vehiculo_id")
    private UUID vehiculoId;

    @Column(name = "tipo_vehiculo", length = 50)
    private String tipoVehiculo;

    @Column(name = "modelo_contrato", length = 100)
    private String modeloContrato;

    @Column(name = "fecha_inicio_transito", nullable = false)
    private LocalDateTime fechaInicioTransito;

    @Column(name = "fecha_cierre", nullable = false)
    private LocalDateTime fechaCierre;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_procesamiento", nullable = false)
    private EstadoProcesamiento estadoProcesamiento;

    @OneToMany(mappedBy = "ruta",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)

    @Builder.Default
    private List<ParadaEntity> paradas = new ArrayList<>();

    public void addParada(ParadaEntity parada) {
        if (parada == null) return;
        if (this.paradas.contains(parada)) return;
        parada.setRuta(this);
        this.paradas.add(parada);
    }

    public void setParadas(List<ParadaEntity> paradas) {
        this.paradas.clear();
        if (paradas != null) {
            paradas.forEach(this::addParada);
        }
    }
}
