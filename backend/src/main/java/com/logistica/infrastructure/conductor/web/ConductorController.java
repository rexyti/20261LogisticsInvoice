package com.logistica.infrastructure.conductor.web;

import com.logistica.application.conductor.dtos.response.ConductorResponseDTO;
import com.logistica.application.conductor.usecases.ValidarConductorUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/conductores")
@RequiredArgsConstructor
public class ConductorController {

    private final ValidarConductorUseCase validarConductorUseCase;

    @GetMapping("/{conductorId}/validar")
    public ResponseEntity<ConductorResponseDTO> validarConductor(@PathVariable UUID conductorId) {
        log.info("Recibida solicitud de validación. conductor_id: {}", conductorId);
        ConductorResponseDTO response = validarConductorUseCase.ejecutar(conductorId);
        return ResponseEntity.ok(response);
    }
}
