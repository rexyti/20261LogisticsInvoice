package com.logistica.application.usecases.contrato;

import com.logistica.application.dtos.response.ContratoResponseDTO;
import com.logistica.domain.repositories.ContratoRepository;
import com.logistica.infrastructure.adapters.ContratoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarContratosUseCase {

    private final ContratoRepository contratoRepository;
    private final ContratoMapper contratoMapper;

    @Transactional(readOnly = true)
    public List<ContratoResponseDTO> ejecutar() {
        return contratoRepository.listar().stream()
                .map(contratoMapper::toResponseDTO)
                .toList();
    }
}
