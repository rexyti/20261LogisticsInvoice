package com.logistica.domain.models;

import com.logistica.domain.enums.EstadoProcesamiento;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class Ruta {
    private UUID rutaId;
    private Transportista transportista;
    private UUID vehiculoId;
    private String tipoVehiculo;
    private String modeloContrato;
    private LocalDateTime fechaInicioTransito;
    private LocalDateTime fechaCierre;
    private EstadoProcesamiento estadoProcesamiento;

    @Builder.Default
    private List<Parada> paradas = new ArrayList<>();
}
