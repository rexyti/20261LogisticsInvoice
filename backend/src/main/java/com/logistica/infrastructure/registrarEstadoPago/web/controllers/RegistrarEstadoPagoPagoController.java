package com.logistica.infrastructure.registrarEstadoPago.web.controllers;

import com.logistica.application.registrarEstadoPago.dtos.response.EventoTransaccionResponseDTO;
import com.logistica.application.registrarEstadoPago.dtos.response.PagoResponseDTO;
import com.logistica.application.registrarEstadoPago.usecases.pago.ObtenerEstadoPagoUseCase;
import com.logistica.application.registrarEstadoPago.usecases.pago.ObtenerEventosTransaccionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class RegistrarEstadoPagoPagoController {

    private final ObtenerEstadoPagoUseCase obtenerEstadoPagoUseCase;
    private final ObtenerEventosTransaccionUseCase obtenerEventosTransaccionUseCase;

    @GetMapping("/{idPago}/estado")
    public ResponseEntity<PagoResponseDTO> obtenerEstadoPago(@PathVariable UUID idPago) {
        return ResponseEntity.ok(obtenerEstadoPagoUseCase.obtenerEstadoPago(idPago));
    }

    @GetMapping("/{idPago}/eventos")
    public ResponseEntity<List<EventoTransaccionResponseDTO>> obtenerEventos(@PathVariable UUID idPago) {
        return ResponseEntity.ok(obtenerEventosTransaccionUseCase.obtenerEventos(idPago));
    }
}
