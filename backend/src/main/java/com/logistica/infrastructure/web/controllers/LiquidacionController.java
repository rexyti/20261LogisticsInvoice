package com.logistica.infrastructure.web.controllers;

import com.logistica.application.dtos.request.RecalcularLiquidacionRequestDTO;
import com.logistica.application.dtos.response.LiquidacionResponseDTO;
import com.logistica.application.usecases.liquidacion.RecalcularLiquidacionUseCase;
import com.logistica.domain.models.Ajuste;
import com.logistica.infrastructure.persistence.mapper.LiquidacionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    public LiquidacionController(RecalcularLiquidacionUseCase recalcularLiquidacionUseCase, LiquidacionMapper liquidacionMapper) {
        this.recalcularLiquidacionUseCase = recalcularLiquidacionUseCase;
        this.liquidacionMapper = liquidacionMapper;
    }

    @PutMapping("/{id}/recalcular")
    @Operation(summary = "Recalcula una liquidación existente añadiendo nuevos ajustes.",
            description = "Este endpoint permite a un administrador recalcular una liquidación. Solo es posible si la liquidación tiene previamente aceptada una solicitud de revisión.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liquidación recalculada exitosamente.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LiquidacionResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "La liquidación especificada no fue encontrada."),
                    @ApiResponse(responseCode = "400", description = "La solicitud de revisión para esta liquidación no ha sido aceptada.")
            })
    public ResponseEntity<LiquidacionResponseDTO> recalcularLiquidacion(
            @PathVariable UUID id,
            @RequestBody RecalcularLiquidacionRequestDTO request) {

        List<Ajuste> nuevosAjustes = request.getAjustes().stream()
                .map(a -> Ajuste.builder()
                        .id(UUID.randomUUID())
                        .tipo(a.getTipo())
                        .monto(a.getMonto())
                        .motivo(a.getMotivo())
                        .build())
                .collect(Collectors.toList());

        com.logistica.domain.models.Liquidacion liquidacion = recalcularLiquidacionUseCase.execute(id, nuevosAjustes, request.getResponsable());

        LiquidacionResponseDTO response = liquidacionMapper.toResponseDTO(liquidacion);
        
        return ResponseEntity.ok(response);
    }
}
