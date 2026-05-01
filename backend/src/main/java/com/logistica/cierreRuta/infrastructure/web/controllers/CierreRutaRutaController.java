package com.logistica.cierreRuta.infrastructure.web.controllers;

import com.logistica.cierreRuta.application.dtos.request.CierreRutaRutaCerradaEventDTO;
import com.logistica.cierreRuta.application.dtos.response.CierreRutaRutaProcesadaResponseDTO;
import com.logistica.cierreRuta.application.usecases.ruta.CierreRutaConsultarRutaUseCase;
import com.logistica.cierreRuta.application.usecases.ruta.CierreRutaProcesarRutaCerradaUseCase;
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
public class CierreRutaRutaController {

    private final CierreRutaConsultarRutaUseCase consultarRutaUseCase;
    private final CierreRutaProcesarRutaCerradaUseCase procesarRutaCerradaUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<CierreRutaRutaProcesadaResponseDTO> obtenerRuta(@PathVariable UUID id) {
        return ResponseEntity.ok(consultarRutaUseCase.ejecutar(id));
    }

    @GetMapping
    public ResponseEntity<Page<CierreRutaRutaProcesadaResponseDTO>> listarRutas(
            @RequestParam(required = false) EstadoProcesamiento estado,
            @PageableDefault(size = 20, sort = "fechaCierre", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(consultarRutaUseCase.listarTodas(estado, pageable));
    }

    @PostMapping("/cerrar")
    public ResponseEntity<Void> cerrarRuta(@RequestBody CierreRutaRutaCerradaEventDTO dto) {
        procesarRutaCerradaUseCase.ejecutar(dto);
        return ResponseEntity.accepted().build();
    }

}
