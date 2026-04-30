package com.logistica.RegistrarEstadoPago.infrastructure.web.controllers;

import com.logistica.RegistrarEstadoPago.application.dtos.request.EventoEstadoPagoRequestDTO;
import com.logistica.RegistrarEstadoPago.application.dtos.response.RecepcionEventoPagoResponseDTO;
import com.logistica.RegistrarEstadoPago.application.usecases.pago.RecibirEventoPagoUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pagos/webhook")
@RequiredArgsConstructor
public class WebhookPagoController {

    private final RecibirEventoPagoUseCase recibirEventoPagoUseCase;

    @PostMapping("/estado")
    public ResponseEntity<RecepcionEventoPagoResponseDTO> recibirEventoEstadoPago(
            @Valid @RequestBody EventoEstadoPagoRequestDTO dto) {
        RecepcionEventoPagoResponseDTO response = recibirEventoPagoUseCase.recibirEvento(dto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
