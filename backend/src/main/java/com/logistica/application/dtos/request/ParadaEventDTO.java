package com.logistica.application.dtos.request;

import lombok.Data;

import java.util.UUID;

@Data
public class ParadaEventDTO {
    private UUID paradaId;
    private String estado;
    private String motivoNoEntrega;
}
