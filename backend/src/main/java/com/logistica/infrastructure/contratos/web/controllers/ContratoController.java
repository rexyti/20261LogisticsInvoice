package com.logistica.infrastructure.contratos.web.controllers;

import com.logistica.application.contratos.dtos.response.ContratoResponseDTO;
import com.logistica.application.contratos.usecases.contrato.ConsultarContratoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/contratos")
@RequiredArgsConstructor
public class ContratoController {

    private final ConsultarContratoUseCase consultarContratoUseCase;

    @GetMapping("/{idContrato}")
    @PreAuthorize("hasAnyRole('GESTOR_FINANCIERO', 'ADMIN')")
    public ResponseEntity<ContratoResponseDTO> buscar(@PathVariable String idContrato) {
        return ResponseEntity.ok(consultarContratoUseCase.buscarPorIdContrato(idContrato));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR_FINANCIERO', 'ADMIN')")
    public ResponseEntity<Page<ContratoResponseDTO>> listar(
            @PageableDefault(size = 20, sort = "fechaInicio", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(consultarContratoUseCase.listar(pageable));
    }

    @GetMapping("/transportista/{idTransportista}")
    @PreAuthorize("hasRole('GESTOR_FINANCIERO')")
    public ResponseEntity<Page<ContratoResponseDTO>> listarPorTransportista(
            @PathVariable UUID idTransportista,
            @PageableDefault(size = 20, sort = "fechaInicio", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(
                consultarContratoUseCase.listarPorTransportista(idTransportista, pageable));
    }
}
