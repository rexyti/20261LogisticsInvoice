package com.logistica.VisualizarLiquidación.infrastructure.web.controllers;

import com.logistica.VisualizarLiquidación.application.dtos.request.FiltroLiquidacionDTO;
import com.logistica.VisualizarLiquidación.application.dtos.response.LiquidacionDetalleDTO;
import com.logistica.VisualizarLiquidación.application.dtos.response.LiquidacionListResponseDTO;
import com.logistica.VisualizarLiquidación.application.security.UsuarioAutenticado;
import com.logistica.VisualizarLiquidación.application.usecases.liquidacion.BuscarLiquidacionesUseCase;
import com.logistica.VisualizarLiquidación.application.usecases.liquidacion.ListarLiquidacionesUseCase;
import com.logistica.VisualizarLiquidación.application.usecases.liquidacion.ObtenerDetalleLiquidacionUseCase;
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
public class LiquidacionController {

    private final ListarLiquidacionesUseCase listarUseCase;
    private final ObtenerDetalleLiquidacionUseCase obtenerDetalleUseCase;
    private final BuscarLiquidacionesUseCase buscarUseCase;

    public LiquidacionController(
            ListarLiquidacionesUseCase listarUseCase,
            ObtenerDetalleLiquidacionUseCase obtenerDetalleUseCase,
            BuscarLiquidacionesUseCase buscarUseCase) {

        this.listarUseCase = listarUseCase;
        this.obtenerDetalleUseCase = obtenerDetalleUseCase;
        this.buscarUseCase = buscarUseCase;
    }

    @GetMapping
    public ResponseEntity<LiquidacionListResponseDTO> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCalculo") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        UsuarioAutenticado usuario = UsuarioAutenticado.from(authentication);

        return ResponseEntity.ok(listarUseCase.ejecutar(pageable, usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LiquidacionDetalleDTO> obtenerDetalle(
            @PathVariable UUID id,
            Authentication authentication) {

        UsuarioAutenticado usuario = UsuarioAutenticado.from(authentication);
        return ResponseEntity.ok(obtenerDetalleUseCase.ejecutar(id, usuario));
    }

    @GetMapping("/buscar")
    public ResponseEntity<LiquidacionDetalleDTO> buscar(
            @Valid @ModelAttribute FiltroLiquidacionDTO filtro,
            Authentication authentication) {

        UsuarioAutenticado usuario = UsuarioAutenticado.from(authentication);
        return ResponseEntity.ok(buscarUseCase.ejecutar(filtro, usuario));
    }
}