package com.logistica.liquidacion.application.dtos.response;

import com.logistica.liquidacion.domain.enums.EstadoLiquidacion;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class LiquidacionResponseDTO {

    private final UUID id;
    private final UUID idRuta;
    private final UUID idContrato;
    private final EstadoLiquidacion estado;
    private final BigDecimal valorBase;
    private final BigDecimal valorFinal;
    private final OffsetDateTime fechaCalculo;
    private final UUID idAdminRevisor;
    private final OffsetDateTime fechaAceptacionRevision;
    private final OffsetDateTime creadoEn;
    private final List<AjusteResponseDTO> ajustes;
}
