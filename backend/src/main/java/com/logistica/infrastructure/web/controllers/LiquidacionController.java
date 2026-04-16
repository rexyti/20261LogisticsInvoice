package com.logistica.infrastructure.web.controllers;

import com.logistica.application.dtos.request.RecalcularLiquidacionRequestDTO;
import com.logistica.application.dtos.response.LiquidacionResponseDTO;
import com.logistica.application.usecases.RecalcularLiquidacionUseCase;
import com.logistica.domain.models.Ajuste;
import com.logistica.domain.models.Liquidacion;
import com.logistica.infrastructure.persistence.mapper.AjusteMapper;
import com.logistica.infrastructure.persistence.mapper.LiquidacionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
