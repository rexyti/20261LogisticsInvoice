package com.logistica.application.dtos.request;

import lombok.Data;

import java.util.UUID;

@Data
public class ConductorEventDTO {
    private UUID conductorId;
    private String nombre;
    private String modeloContrato;
}
