package com.logistica.application.contratos.usecases.contrato;

import com.logistica.application.contratos.dtos.response.ContratoResponseDTO;
import com.logistica.application.contratos.mappers.ContratoResponseMapper;
import com.logistica.domain.contratos.exceptions.RecursoNoEncontradoException;
import com.logistica.domain.contratos.repositories.ContratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultarContratoUseCase {

    private final ContratoRepository contratoRepository;
    private final ContratoResponseMapper responseMapper;

    public ContratoResponseDTO buscarPorIdContrato(String idContrato) {
        return contratoRepository.buscarPorIdContrato(idContrato)
                .map(responseMapper::toResponseDTO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Contrato no encontrado: " + idContrato));
    }

    public Page<ContratoResponseDTO> listar(Pageable pageable) {
        return contratoRepository.listar(pageable).map(responseMapper::toResponseDTO);
    }

    public Page<ContratoResponseDTO> listarPorTransportista(UUID idTransportista, Pageable pageable) {
        return contratoRepository.listarPorTransportista(idTransportista, pageable)
                .map(responseMapper::toResponseDTO);
    }
}
