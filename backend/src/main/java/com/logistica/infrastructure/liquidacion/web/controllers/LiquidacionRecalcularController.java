package com.logistica.infrastructure.liquidacion.web.controllers;

import com.logistica.application.liquidacion.dtos.request.RecalcularRequestDTO;
import com.logistica.application.liquidacion.dtos.response.LiquidacionResponseDTO;
import com.logistica.application.liquidacion.usecases.LiquidacionRecalcularUseCase;
import com.logistica.domain.liquidacion.models.Ajuste;
import com.logistica.domain.liquidacion.models.Liquidacion;
import com.logistica.infrastructure.liquidacion.persistence.mapper.AjusteMapper;
import com.logistica.infrastructure.liquidacion.persistence.mapper.Mapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/liquidaciones")
@Tag(name = "Liquidaciones", description = "Gestión y recálculo de liquidaciones.")
@RequiredArgsConstructor
public class LiquidacionRecalcularController {

    private final LiquidacionRecalcularUseCase recalcularUseCase;
    private final Mapper liquidacionMapper;
    private final AjusteMapper ajusteMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/recalcular")
    public ResponseEntity<LiquidacionResponseDTO> recalcularLiquidacion(
            @PathVariable UUID id,
            @Valid @RequestBody RecalcularRequestDTO request) {

        List<Ajuste> nuevosAjustes = ajusteMapper.toModelList(request.getAjustes());
        Liquidacion liquidacion = recalcularUseCase.execute(id, nuevosAjustes, request.getResponsable());

        return ResponseEntity.ok(liquidacionMapper.toResponseDTO(liquidacion));
    }
}
