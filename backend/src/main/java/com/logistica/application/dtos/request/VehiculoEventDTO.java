package com.logistica.application.dtos.request;

import lombok.Data;

import java.util.UUID;

@Data
public class VehiculoEventDTO {
    private UUID vehiculoId;
    private String tipo;
}
