package com.logistica.contratos.domain.models;


import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class Seguro {
    private UUID idSeguro;
    private String numeroPoliza;
    private String estado;
}
