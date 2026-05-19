package com.logistica.infrastructure.messaging.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransportistaMensajeDTO {

    // Acepta "transportista_id" (contrato) y "conductor_id" (ruta cerrada)
    @JsonAlias("conductor_id")
    private UUID transportistaId;

    private String nombre;

    // Presente solo en ruta cerrada; null en contrato creado
    private String modeloContrato;
}
