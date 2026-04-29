package com.logistica.liquidacion.infrastructure.web.controllers;

import com.logistica.liquidacion.application.dtos.request.CierreRutaEventDTO;
import com.logistica.liquidacion.application.dtos.response.LiquidacionResponseDTO;
import com.logistica.liquidacion.application.usecases.CalcularLiquidacionUseCase;
import com.logistica.liquidacion.domain.models.Liquidacion;
import com.logistica.liquidacion.domain.models.LiquidacionRuta;
import com.logistica.liquidacion.infrastructure.persistence.mapper.LiquidacionMapper;
import com.logistica.liquidacion.infrastructure.persistence.mapper.LiquidacionRutaMapper;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eventos")
@Tag(name = "Eventos", description = "Endpoints para la recepción de eventos del sistema.")
public class EventoController {

    private final CalcularLiquidacionUseCase calcularLiquidacionUseCase;
    private final LiquidacionMapper liquidacionMapper;
    private final LiquidacionRutaMapper rutaMapper;

    public EventoController(
            CalcularLiquidacionUseCase calcularLiquidacionUseCase,
            LiquidacionMapper liquidacionMapper,
            LiquidacionRutaMapper rutaMapper
    ) {
        this.calcularLiquidacionUseCase = calcularLiquidacionUseCase;
        this.liquidacionMapper = liquidacionMapper;
        this.rutaMapper = rutaMapper;
    }

    @PostMapping("/cierre-ruta")
    public ResponseEntity<LiquidacionResponseDTO> handleCierreRuta(
            @RequestBody @Valid CierreRutaEventDTO event) {

        LiquidacionRuta ruta = rutaMapper.toModel(event);

        Liquidacion liquidacion = calcularLiquidacionUseCase
                .execute(ruta, event.getIdContrato());

        return ResponseEntity.ok(
                liquidacionMapper.toResponseDTO(liquidacion)
        );
    }
}