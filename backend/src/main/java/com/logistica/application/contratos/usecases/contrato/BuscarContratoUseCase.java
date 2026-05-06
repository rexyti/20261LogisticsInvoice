package com.logistica.application.contratos.usecases.contrato;

import com.logistica.application.contratos.dtos.response.ContratoResponseDTO;
import com.logistica.application.contratos.mappers.ContratoResponseMapper;
import com.logistica.domain.contratos.exceptions.ContratoNotFoundException;
import com.logistica.domain.contratos.repositories.ContratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarContratoUseCase {

    private final ContratoRepository contratoRepository;
    private final ContratoResponseMapper responseMapper;

    public ContratoResponseDTO ejecutar(String idContrato) {
        return contratoRepository.buscarPorIdContrato(idContrato)
                .map(responseMapper::toResponseDTO)
                .orElseThrow(() -> new ContratoNotFoundException(idContrato));
    }
}