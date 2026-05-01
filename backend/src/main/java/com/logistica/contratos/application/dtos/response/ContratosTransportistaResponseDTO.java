package com.logistica.contratos.application.dtos.response;

import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
public class ContratosTransportistaResponseDTO {
    private UUID transportistaId;
    private String nombre;
}