package com.logistica.infrastructure.web.controllers;

import com.logistica.application.usecases.pago.ConsultarEstadoPagoUseCase;
import com.logistica.domain.exceptions.PagoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private ConsultarEstadoPagoUseCase consultarEstadoPagoUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerEstadoPago(@PathVariable("id") UUID id) {
        try {
            var estadoPago = consultarEstadoPagoUseCase.ejecutar(id);
            return ResponseEntity.ok(estadoPago);
        } catch (PagoNoEncontradoException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
