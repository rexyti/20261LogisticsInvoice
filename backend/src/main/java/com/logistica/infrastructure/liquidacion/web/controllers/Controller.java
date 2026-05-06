package com.logistica.infrastructure.liquidacion.web.controllers;

import com.logistica.application.liquidacion.dtos.request.RecalcularRequestDTO;
import com.logistica.application.liquidacion.dtos.response.ResponseDTO;
import com.logistica.application.liquidacion.usecases.RecalcularUseCase;
import com.logistica.domain.liquidacion.models.Ajuste;
import com.logistica.domain.liquidacion.models.Liquidacion;
import com.logistica.infrastructure.liquidacion.persistence.mapper.AjusteMapper;
import com.logistica.infrastructure.liquidacion.persistence.mapper.Mapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/liquidaciones")
@Tag(name = "Liquidaciones", description = "Endpoints para la gestión de liquidaciones.")
public class Controller {

    private final RecalcularUseCase recalcularLiquidacionUseCase;
    private final Mapper liquidacionMapper;
    private final AjusteMapper ajusteMapper;

    public Controller(
            RecalcularUseCase recalcularLiquidacionUseCase,
            Mapper liquidacionMapper,
            AjusteMapper ajusteMapper
    ) {
        this.recalcularLiquidacionUseCase = recalcularLiquidacionUseCase;
        this.liquidacionMapper = liquidacionMapper;
        this.ajusteMapper = ajusteMapper;
    }

    @PutMapping("/{id}/recalcular")
    public ResponseEntity<ResponseDTO> recalcularLiquidacion(
            @PathVariable UUID id,
            @RequestBody @Valid RecalcularRequestDTO request) {

        List<Ajuste> nuevosAjustes = ajusteMapper.toModelList(request.getAjustes());

        Liquidacion liquidacion = recalcularLiquidacionUseCase
                .execute(id, nuevosAjustes, request.getResponsable());

        return ResponseEntity.ok(
                liquidacionMapper.toResponseDTO(liquidacion)
        );
    }
}
