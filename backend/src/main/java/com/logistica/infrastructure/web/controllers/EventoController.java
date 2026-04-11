package com.logistica.infrastructure.web.controllers;

import com.logistica.application.dtos.request.CierreRutaEventDTO;
import com.logistica.application.dtos.response.LiquidacionResponseDTO;
import com.logistica.application.usecases.liquidacion.CalcularLiquidacionUseCase;
import com.logistica.domain.models.Contrato;
import com.logistica.domain.models.Paquete;
import com.logistica.domain.models.Ruta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final CalcularLiquidacionUseCase calcularLiquidacionUseCase;
    private final com.logistica.infrastructure.adapters.LiquidacionMapper liquidacionMapper;

    public EventoController(CalcularLiquidacionUseCase calcularLiquidacionUseCase, com.logistica.infrastructure.adapters.LiquidacionMapper liquidacionMapper) {
        this.calcularLiquidacionUseCase = calcularLiquidacionUseCase;
        this.liquidacionMapper = liquidacionMapper;
    }

    @PostMapping("/cierre-ruta")
    public ResponseEntity<LiquidacionResponseDTO> handleCierreRuta(@RequestBody CierreRutaEventDTO event) {
        // Mapear DTO a modelos de dominio
        Ruta ruta = new Ruta(
                event.getIdRuta(),
                null, // fechaInicio no es necesaria para el cálculo
                event.getFechaCierre(),
                event.getPaquetes().stream()
                        .map(p -> new Paquete(p.getId(), p.getEstadoFinal(), p.getNovedades()))
                        .collect(Collectors.toList())
        );

        Contrato contrato = new Contrato(
                event.getIdContrato(),
                event.getTipoContratacion(),
                null // La tarifa se obtiene de la base de datos, no del evento
        );

        com.logistica.domain.models.Liquidacion liquidacion = calcularLiquidacionUseCase.execute(ruta, contrato);
        
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
