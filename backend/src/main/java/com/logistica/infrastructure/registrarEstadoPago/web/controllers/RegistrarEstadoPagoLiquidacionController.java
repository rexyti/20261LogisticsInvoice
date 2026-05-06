package com.logistica.infrastructure.registrarEstadoPago.web.controllers;

import com.logistica.application.registrarEstadoPago.dtos.response.PagoResponseDTO;
import com.logistica.application.registrarEstadoPago.usecases.pago.ObtenerEstadoPagoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/liquidaciones")
@RequiredArgsConstructor
public class RegistrarEstadoPagoLiquidacionController {

    private final ObtenerEstadoPagoUseCase obtenerEstadoPagoUseCase;

    @GetMapping("/{idLiquidacion}/pago/estado")
    public ResponseEntity<PagoResponseDTO> obtenerEstadoPagoPorLiquidacion(@PathVariable UUID idLiquidacion) {
        return ResponseEntity.ok(obtenerEstadoPagoUseCase.obtenerEstadoPagoPorLiquidacion(idLiquidacion));
    }
}
