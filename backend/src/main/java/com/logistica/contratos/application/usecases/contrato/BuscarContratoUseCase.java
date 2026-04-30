package com.logistica.contratos.application.usecases.contrato;

import com.logistica.contratos.application.dtos.response.ContratoResponseDTO;
import com.logistica.contratos.application.mappers.ContratoResponseMapper;
import com.logistica.contratos.domain.exceptions.ContratoNotFoundException;
import com.logistica.contratos.domain.repositories.ContratoRepository;
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