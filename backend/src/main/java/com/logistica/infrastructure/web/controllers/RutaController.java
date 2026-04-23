package com.logistica.infrastructure.web.controllers;

import com.logistica.application.dtos.response.RutaProcesadaResponseDTO;
import com.logistica.application.usecases.ruta.ConsultarRutaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
public class RutaController {

    private final ConsultarRutaUseCase consultarRutaUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<RutaProcesadaResponseDTO> obtenerRuta(@PathVariable UUID id) {
        return ResponseEntity.ok(consultarRutaUseCase.ejecutar(id));
    }

    @GetMapping
    public ResponseEntity<List<RutaProcesadaResponseDTO>> listarRutas() {
        return ResponseEntity.ok(consultarRutaUseCase.listarTodas());
    }
}
