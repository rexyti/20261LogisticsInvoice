package com.logistica.infrastructure.liquidacion.web.controllers;

import com.logistica.application.liquidacion.dtos.request.CierreRutaEventDTO;
import com.logistica.application.liquidacion.dtos.response.LiquidacionResponseDTO;
import com.logistica.application.liquidacion.usecases.LiquidacionCalcularUseCase;
import com.logistica.domain.liquidacion.models.Liquidacion;
import com.logistica.domain.liquidacion.models.RutaLiquidacion;
import com.logistica.infrastructure.liquidacion.persistence.mapper.Mapper;
import com.logistica.infrastructure.liquidacion.persistence.mapper.LiquidacionRutaMapper;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eventos")
@Tag(name = "Eventos", description = "Endpoints para la recepción de eventos del sistema.")
public class EventoController {

    private final LiquidacionCalcularUseCase calcularLiquidacionUseCase;
    private final Mapper liquidacionMapper;
    private final LiquidacionRutaMapper rutaMapper;

    public EventoController(
            LiquidacionCalcularUseCase calcularLiquidacionUseCase,
            Mapper liquidacionMapper,
            LiquidacionRutaMapper rutaMapper
    ) {
        this.calcularLiquidacionUseCase = calcularLiquidacionUseCase;
        this.liquidacionMapper = liquidacionMapper;
        this.rutaMapper = rutaMapper;
    }

    @PostMapping("/cierre-ruta")
    public ResponseEntity<LiquidacionResponseDTO> handleCierreRuta(
            @RequestBody @Valid CierreRutaEventDTO event) {

        RutaLiquidacion ruta = rutaMapper.toModel(event);

        Liquidacion liquidacion = calcularLiquidacionUseCase
                .execute(ruta, event.getIdContrato());

        return ResponseEntity.ok(
                liquidacionMapper.toResponseDTO(liquidacion)
        );
    }
}
