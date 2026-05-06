package com.logistica.infrastructure.visualizarEstadoPago.web.controllers;

import com.logistica.application.visualizarEstadoPago.dtos.response.VisualizarEstadoPagoEstadoPagoResponseDTO;
import com.logistica.application.visualizarEstadoPago.dtos.response.PagoListDTO;
import com.logistica.application.visualizarEstadoPago.usecases.pago.ConsultarEstadoPagoUseCase;
import com.logistica.application.visualizarEstadoPago.usecases.pago.ListarPagosUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pagos")
public class VisualizarEstadoPagoPagoController {

    @Autowired
    private ConsultarEstadoPagoUseCase consultarEstadoPagoUseCase;

    @Autowired
    private ListarPagosUseCase listarPagosUseCase;

    @GetMapping
    public ResponseEntity<List<PagoListDTO>> listarPagos(Authentication authentication) {
        UUID usuarioId = UUID.fromString(authentication.getName());
        List<PagoListDTO> pagos = listarPagosUseCase.ejecutar(usuarioId);
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisualizarEstadoPagoEstadoPagoResponseDTO> obtenerEstadoPago(
            @PathVariable("id") UUID id, Authentication authentication) {
        UUID usuarioId = UUID.fromString(authentication.getName());
        VisualizarEstadoPagoEstadoPagoResponseDTO estadoPago = consultarEstadoPagoUseCase.ejecutar(id, usuarioId);
        return ResponseEntity.ok(estadoPago);
    }
}
