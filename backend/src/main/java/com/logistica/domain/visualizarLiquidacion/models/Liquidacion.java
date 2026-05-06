package com.logistica.domain.visualizarLiquidacion.models;

import com.logistica.domain.visualizarLiquidacion.enums.EstadoLiquidacion;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class Liquidacion {
    private UUID id;
    private UUID idRuta;
    private UUID idContrato;
    private EstadoLiquidacion estadoLiquidacion;
    private BigDecimal montoBruto;
    private BigDecimal montoNeto;
    private LocalDateTime fechaCalculo;
    private String usuarioId;
    private Ruta ruta;
    private List<Ajuste> ajustes;
}
