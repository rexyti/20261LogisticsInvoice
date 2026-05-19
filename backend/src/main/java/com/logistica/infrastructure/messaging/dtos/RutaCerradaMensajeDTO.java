package com.logistica.infrastructure.messaging.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RutaCerradaMensajeDTO {
    private String tipoEvento;
    private UUID rutaId;
    private LocalDateTime fechaHoraInicioTransito;
    private LocalDateTime fechaHoraCierre;
    private TransportistaMensajeDTO conductor;
    private VehiculoMensajeDTO vehiculo;
    private List<ParadaMensajeDTO> paradas;
}
