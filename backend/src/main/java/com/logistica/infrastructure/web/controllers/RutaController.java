package com.logistica.infrastructure.web.controllers;

import com.logistica.application.dtos.response.RutaProcesadaResponseDTO;
import com.logistica.application.usecases.ruta.ConsultarRutaUseCase;
import com.logistica.domain.exceptions.RutaNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rutas")
@RequiredArgsConstructor
public class RutaController {

    private final ConsultarRutaUseCase consultarRutaUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<RutaProcesadaResponseDTO> obtenerRuta(@PathVariable UUID id) {
        RutaProcesadaResponseDTO ruta = consultarRutaUseCase.ejecutar(id);
        return ResponseEntity.ok(ruta);
    }

    @GetMapping
    public ResponseEntity<List<RutaProcesadaResponseDTO>> listarRutas() {
        List<RutaProcesadaResponseDTO> rutas = consultarRutaUseCase.listarTodas();
        return ResponseEntity.ok(rutas);
    }


    @ExceptionHandler(RutaNotFoundException.class)
    public ResponseEntity<String> handleRutaNotFound(RutaNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}