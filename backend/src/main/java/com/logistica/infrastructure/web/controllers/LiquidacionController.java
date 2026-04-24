package com.logistica.infrastructure.web.controllers;

import com.logistica.application.dtos.request.FiltroLiquidacionDTO;
import com.logistica.application.dtos.response.LiquidacionDetalleDTO;
import com.logistica.application.dtos.response.LiquidacionListResponseDTO;
import com.logistica.application.security.UsuarioAutenticado;
import com.logistica.application.usecases.liquidacion.BuscarLiquidacionesUseCase;
import com.logistica.application.usecases.liquidacion.ListarLiquidacionesUseCase;
import com.logistica.application.usecases.liquidacion.ObtenerDetalleLiquidacionUseCase;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/liquidaciones")
public class LiquidacionController {

    private static final Set<String> CAMPOS_ORDENABLES =
            Set.of("fechaCalculo", "montoBruto", "montoNeto", "estadoLiquidacion");
    private static final String CAMPO_ORDEN_DEFAULT = "fechaCalculo";

    private final ListarLiquidacionesUseCase listarUseCase;
    private final ObtenerDetalleLiquidacionUseCase obtenerDetalleUseCase;
    private final BuscarLiquidacionesUseCase buscarUseCase;

    public LiquidacionController(ListarLiquidacionesUseCase listarUseCase,
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

        String campoOrden = CAMPOS_ORDENABLES.contains(sortBy) ? sortBy : CAMPO_ORDEN_DEFAULT;
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(campoOrden).ascending()
                : Sort.by(campoOrden).descending();
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
