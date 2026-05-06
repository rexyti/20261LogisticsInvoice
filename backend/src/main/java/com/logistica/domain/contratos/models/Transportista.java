package com.logistica.domain.contratos.models;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class Transportista {
    private UUID transportistaId;
    private String nombre;
}
