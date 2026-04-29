package com.logistica.cierreRuta.application.dtos.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class TransportistaResponseDTO {
    private UUID TransportistaId;
    private String nombre;
}
