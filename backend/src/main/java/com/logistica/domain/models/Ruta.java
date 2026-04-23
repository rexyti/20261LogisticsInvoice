package com.logistica.domain.models;

import com.logistica.domain.enums.EstadoProcesamiento;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class Ruta {
    private UUID rutaId;
    private Transportista transportista;
    private String tipoVehiculo;
    private String modeloContrato;
    private LocalDateTime fechaInicioTransito;
    private LocalDateTime fechaCierre;
    private EstadoProcesamiento estadoProcesamiento;
    private List<Parada> paradas;
}
