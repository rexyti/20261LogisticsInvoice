package com.logistica.application.dtos.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class RutaCerradaEventDTO {
    private String tipoEvento;
    private UUID rutaId;
    private LocalDateTime fechaHoraInicioTransito;
    private LocalDateTime fechaHoraCierre;
    private ConductorEventDTO conductor;
    private VehiculoEventDTO vehiculo;
    private List<ParadaEventDTO> paradas;
}
