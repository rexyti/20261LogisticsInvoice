package com.logistica.infrastructure.registrarEstadoPago.web.controllers;

import com.logistica.application.registrarEstadoPago.dtos.request.IniciarPagoRequestDTO;
import com.logistica.application.registrarEstadoPago.dtos.response.IniciarPagoResponseDTO;
import com.logistica.application.registrarEstadoPago.dtos.response.PagoResponseDTO;
import com.logistica.application.registrarEstadoPago.usecases.pago.IniciarPagoBancarioUseCase;
import com.logistica.application.registrarEstadoPago.usecases.pago.ObtenerEstadoPagoUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/liquidaciones")
@RequiredArgsConstructor
public class RegistrarEstadoPagoLiquidacionController {

    private final ObtenerEstadoPagoUseCase obtenerEstadoPagoUseCase;
    private final IniciarPagoBancarioUseCase iniciarPagoBancarioUseCase;

    @PostMapping("/{idLiquidacion}/pago/banco")
    public ResponseEntity<IniciarPagoResponseDTO> iniciarPago(
            @PathVariable UUID idLiquidacion,
            @Valid @RequestBody IniciarPagoRequestDTO request) {
        log.info("Recibida solicitud de pago bancario. id_liquidacion: {}, monto: {}",
                idLiquidacion, request.monto());
        IniciarPagoResponseDTO response = iniciarPagoBancarioUseCase.ejecutar(idLiquidacion, request.monto());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{idLiquidacion}/pago/estado")
    public ResponseEntity<PagoResponseDTO> obtenerEstadoPagoPorLiquidacion(@PathVariable UUID idLiquidacion) {
        return ResponseEntity.ok(obtenerEstadoPagoUseCase.obtenerEstadoPagoPorLiquidacion(idLiquidacion));
    }
}
