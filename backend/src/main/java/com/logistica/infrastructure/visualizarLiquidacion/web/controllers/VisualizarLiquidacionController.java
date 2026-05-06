package com.logistica.infrastructure.visualizarLiquidacion.web.controllers;

import com.logistica.application.visualizarLiquidacion.dtos.request.VisualizarLiquidacionFiltroDTO;
import com.logistica.application.visualizarLiquidacion.dtos.response.VisualizarLiquidacionDetalleDTO;
import com.logistica.application.visualizarLiquidacion.dtos.response.VisualizarLiquidacionListResponseDTO;
import com.logistica.application.visualizarLiquidacion.security.VisualizarLiquidacionUsuarioAutenticado;
import com.logistica.application.visualizarLiquidacion.usecases.liquidacion.VisualizarLiquidacionBuscarUseCase;
import com.logistica.application.visualizarLiquidacion.usecases.liquidacion.VisualizarLiquidacionListarUseCase;
import com.logistica.application.visualizarLiquidacion.usecases.liquidacion.VisualizarLiquidacionObtenerDetalleUseCase;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/liquidaciones")
public class VisualizarLiquidacionController {

    private final VisualizarLiquidacionListarUseCase listarUseCase;
    private final VisualizarLiquidacionObtenerDetalleUseCase obtenerDetalleUseCase;
    private final VisualizarLiquidacionBuscarUseCase buscarUseCase;

    public VisualizarLiquidacionController(
            VisualizarLiquidacionListarUseCase listarUseCase,
            VisualizarLiquidacionObtenerDetalleUseCase obtenerDetalleUseCase,
            VisualizarLiquidacionBuscarUseCase buscarUseCase) {

        this.listarUseCase = listarUseCase;
        this.obtenerDetalleUseCase = obtenerDetalleUseCase;
        this.buscarUseCase = buscarUseCase;
    }

    @GetMapping
    public ResponseEntity<VisualizarLiquidacionListResponseDTO> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCalculo") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        VisualizarLiquidacionUsuarioAutenticado usuario = VisualizarLiquidacionUsuarioAutenticado.from(authentication);

        return ResponseEntity.ok(listarUseCase.ejecutar(pageable, usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisualizarLiquidacionDetalleDTO> obtenerDetalle(
            @PathVariable UUID id,
            Authentication authentication) {

        VisualizarLiquidacionUsuarioAutenticado usuario = VisualizarLiquidacionUsuarioAutenticado.from(authentication);
        return ResponseEntity.ok(obtenerDetalleUseCase.ejecutar(id, usuario));
    }

    @GetMapping("/buscar")
    public ResponseEntity<VisualizarLiquidacionDetalleDTO> buscar(
            @Valid @ModelAttribute VisualizarLiquidacionFiltroDTO filtro,
            Authentication authentication) {

        VisualizarLiquidacionUsuarioAutenticado usuario = VisualizarLiquidacionUsuarioAutenticado.from(authentication);
        return ResponseEntity.ok(buscarUseCase.ejecutar(filtro, usuario));
    }
}
