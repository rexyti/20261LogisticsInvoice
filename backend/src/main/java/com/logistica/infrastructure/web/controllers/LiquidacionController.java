package com.logistica.infrastructure.web.controllers;

import com.logistica.application.dtos.request.RecalcularLiquidacionRequestDTO;
import com.logistica.application.dtos.response.LiquidacionResponseDTO;
import com.logistica.application.usecases.liquidacion.RecalcularLiquidacionUseCase;
import com.logistica.domain.models.Ajuste;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/liquidaciones")
public class LiquidacionController {

    private final RecalcularLiquidacionUseCase recalcularLiquidacionUseCase;
    private final com.logistica.infrastructure.adapters.LiquidacionMapper liquidacionMapper;

    public LiquidacionController(RecalcularLiquidacionUseCase recalcularLiquidacionUseCase, com.logistica.infrastructure.adapters.LiquidacionMapper liquidacionMapper) {
        this.recalcularLiquidacionUseCase = recalcularLiquidacionUseCase;
        this.liquidacionMapper = liquidacionMapper;
    }

    @PutMapping("/{id}/recalcular")
    public ResponseEntity<LiquidacionResponseDTO> recalcularLiquidacion(
            @PathVariable UUID id,
            @RequestBody RecalcularLiquidacionRequestDTO request) {

        // Mapear DTO a modelos de dominio
        List<Ajuste> nuevosAjustes = request.getAjustes().stream()
                .map(a -> new Ajuste(null, id, a.getTipo(), a.getMonto(), a.getMotivo()))
                .collect(Collectors.toList());

        com.logistica.domain.models.Liquidacion liquidacion = recalcularLiquidacionUseCase.execute(id, nuevosAjustes, request.getResponsable());

        // Mapear modelo de dominio a DTO de respuesta
        LiquidacionResponseDTO response = new LiquidacionResponseDTO();
        response.setId(liquidacion.getId());
        response.setIdRuta(liquidacion.getIdRuta());
        response.setEstado(liquidacion.getEstado());
        response.setValorFinal(liquidacion.getValorFinal());
        response.setFechaCalculo(liquidacion.getFechaCalculo());
        
        return ResponseEntity.ok(response);
    }
}
