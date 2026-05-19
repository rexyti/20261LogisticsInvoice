package com.logistica.infrastructure.messaging.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ParadaMensajeDTO {
    private UUID paradaId;
    private UUID paqueteId;
    private String estado;
    private String motivoNoEntrega;
    private LocalDateTime fechaHoraGestion;
}
