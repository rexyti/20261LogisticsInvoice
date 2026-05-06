package com.logistica.application.contratos.usecases.contrato;

import com.logistica.application.contratos.dtos.response.ContratoResponseDTO;
import com.logistica.application.contratos.mappers.ContratoResponseMapper;
import com.logistica.domain.contratos.repositories.ContratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ListarContratosUseCase {

    private final ContratoRepository contratoRepository;
    private final ContratoResponseMapper responseMapper;      // ← application, no infra

    public Page<ContratoResponseDTO> ejecutar(Pageable pageable) {
        return contratoRepository.listar(pageable)
                .map(responseMapper::toResponseDTO);
    }
}