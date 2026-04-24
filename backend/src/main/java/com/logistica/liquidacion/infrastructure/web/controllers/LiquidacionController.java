package com.logistica.liquidacion.infrastructure.web.controllers;

import com.logistica.liquidacion.application.dtos.request.RecalcularLiquidacionRequestDTO;
import com.logistica.liquidacion.application.dtos.response.LiquidacionResponseDTO;
import com.logistica.liquidacion.application.usecases.RecalcularLiquidacionUseCase;
import com.logistica.liquidacion.domain.models.Ajuste;
import com.logistica.liquidacion.domain.models.Liquidacion;
import com.logistica.liquidacion.infrastructure.persistence.mapper.AjusteMapper;
import com.logistica.liquidacion.infrastructure.persistence.mapper.LiquidacionMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/liquidaciones")
@Tag(name = "Liquidaciones", description = "Endpoints para la gestión de liquidaciones.")
public class LiquidacionController {

    private final RecalcularLiquidacionUseCase recalcularLiquidacionUseCase;
    private final LiquidacionMapper liquidacionMapper;
    private final AjusteMapper ajusteMapper;

    public LiquidacionController(
            RecalcularLiquidacionUseCase recalcularLiquidacionUseCase,
            LiquidacionMapper liquidacionMapper,
            AjusteMapper ajusteMapper
    ) {
        this.recalcularLiquidacionUseCase = recalcularLiquidacionUseCase;
        this.liquidacionMapper = liquidacionMapper;
        this.ajusteMapper = ajusteMapper;
    }

    @PutMapping("/{id}/recalcular")
    public ResponseEntity<LiquidacionResponseDTO> recalcularLiquidacion(
            @PathVariable UUID id,
            @RequestBody @Valid RecalcularLiquidacionRequestDTO request) {

        List<Ajuste> nuevosAjustes = ajusteMapper.toModelList(request.getAjustes());

        Liquidacion liquidacion = recalcularLiquidacionUseCase
                .execute(id, nuevosAjustes, request.getResponsable());

        return ResponseEntity.ok(
                liquidacionMapper.toResponseDTO(liquidacion)
        );
    }
}
