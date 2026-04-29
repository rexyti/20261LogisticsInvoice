package com.logistica.cierreRuta.infrastructure.web.controllers;

import com.logistica.cierreRuta.application.dtos.request.RutaCerradaEventDTO;
import com.logistica.cierreRuta.application.dtos.response.RutaProcesadaResponseDTO;
import com.logistica.cierreRuta.application.usecases.ruta.ConsultarRutaUseCase;
import com.logistica.cierreRuta.application.usecases.ruta.ProcesarRutaCerradaUseCase;
import com.logistica.cierreRuta.domain.enums.EstadoProcesamiento;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rutas")
@RequiredArgsConstructor
public class RutaController {

    private final ConsultarRutaUseCase consultarRutaUseCase;
    private final ProcesarRutaCerradaUseCase procesarRutaCerradaUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<RutaProcesadaResponseDTO> obtenerRuta(@PathVariable UUID id) {
        return ResponseEntity.ok(consultarRutaUseCase.ejecutar(id));
    }

    @GetMapping
    public ResponseEntity<Page<RutaProcesadaResponseDTO>> listarRutas(
            @RequestParam(required = false) EstadoProcesamiento estado,
            @PageableDefault(size = 20, sort = "fechaCierre", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(consultarRutaUseCase.listarTodas(estado, pageable));
    }

    @PostMapping("/cerrar")
    public ResponseEntity<Void> cerrarRuta(@RequestBody RutaCerradaEventDTO dto) {
        procesarRutaCerradaUseCase.ejecutar(dto);
        return ResponseEntity.accepted().build();
    }

}