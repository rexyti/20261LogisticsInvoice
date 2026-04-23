package com.logistica.application.usecases.contrato;

import com.logistica.application.dtos.response.ContratoResponseDTO;
import com.logistica.domain.exceptions.RecursoNoEncontradoException;
import com.logistica.domain.repositories.ContratoRepository;
import com.logistica.infrastructure.adapters.ContratoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuscarContratoUseCase {

    private final ContratoRepository contratoRepository;
    private final ContratoMapper contratoMapper;

    @Transactional(readOnly = true)
    public ContratoResponseDTO ejecutar(String idContrato) {
        return contratoRepository.buscarPorIdContrato(idContrato)
                .map(contratoMapper::toResponseDTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contrato no encontrado"));
    }
}
