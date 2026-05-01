package com.logistica.VisualizarLiquidación.domain.models;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class Ajuste {
    private UUID id;
    private String tipo;
    private BigDecimal monto;
    private String razon;
}
