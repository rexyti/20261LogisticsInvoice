package com.logistica.contratos.domain.models;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ContratosTransportista {
    private UUID transportistaId;
    private String nombre;
}
