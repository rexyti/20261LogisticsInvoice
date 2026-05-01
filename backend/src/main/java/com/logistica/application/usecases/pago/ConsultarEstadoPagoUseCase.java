package com.logistica.application.usecases.pago;

import com.logistica.application.dtos.response.EstadoPagoResponseDTO;
import com.logistica.application.mappers.PagoDtoMapper;
import com.logistica.domain.exceptions.PagoNoEncontradoException;
import com.logistica.domain.models.Pago;
import com.logistica.domain.repositories.PagoRepository;

import java.util.UUID;

public class ConsultarEstadoPagoUseCase {

    private final PagoRepository pagoRepository;
    private final PagoDtoMapper pagoDtoMapper;

    public ConsultarEstadoPagoUseCase(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
        this.pagoDtoMapper = new PagoDtoMapper();
    }

    public EstadoPagoResponseDTO ejecutar(UUID pagoId) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new PagoNoEncontradoException("Pago no encontrado"));
        return pagoDtoMapper.toEstadoPagoResponseDTO(pago);
    }
}
