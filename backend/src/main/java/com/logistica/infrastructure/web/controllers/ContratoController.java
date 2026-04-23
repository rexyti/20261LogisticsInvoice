package com.logistica.infrastructure.web.controllers;

import com.logistica.application.dtos.request.ContratoRequestDTO;
import com.logistica.application.dtos.response.ContratoResponseDTO;
import com.logistica.application.usecases.contrato.BuscarContratoUseCase;
import com.logistica.application.usecases.contrato.CrearContratoUseCase;
import com.logistica.application.usecases.contrato.ListarContratosUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contratos")
@RequiredArgsConstructor
public class ContratoController {

    private final CrearContratoUseCase crearContratoUseCase;
    private final BuscarContratoUseCase buscarContratoUseCase;
    private final ListarContratosUseCase listarContratosUseCase;

    @PostMapping
    @PreAuthorize("hasRole('GESTOR_TARIFAS')")
    public ResponseEntity<ContratoResponseDTO> registrar(@Valid @RequestBody ContratoRequestDTO dto) {
        ContratoResponseDTO response = crearContratoUseCase.ejecutar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{idContrato}")
    @PreAuthorize("hasRole('GESTOR_TARIFAS')")
    public ResponseEntity<ContratoResponseDTO> buscar(@PathVariable String idContrato) {
        return ResponseEntity.ok(buscarContratoUseCase.ejecutar(idContrato));
    }

    @GetMapping
    @PreAuthorize("hasRole('GESTOR_TARIFAS')")
    public ResponseEntity<List<ContratoResponseDTO>> listar() {
        return ResponseEntity.ok(listarContratosUseCase.ejecutar());
    }
}
