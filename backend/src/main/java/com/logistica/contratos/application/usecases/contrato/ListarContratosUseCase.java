package com.logistica.contratos.application.usecases.contrato;

import com.logistica.contratos.application.dtos.response.ContratoResponseDTO;
import com.logistica.contratos.application.mappers.ContratoResponseMapper;
import com.logistica.contratos.domain.repositories.ContratoRepository;
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