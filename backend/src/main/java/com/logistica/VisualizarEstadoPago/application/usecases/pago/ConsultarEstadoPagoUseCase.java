package com.logistica.VisualizarEstadoPago.application.usecases.pago;

import com.logistica.VisualizarEstadoPago.application.dtos.response.EstadoPagoResponseDTO;
import com.logistica.VisualizarEstadoPago.application.mappers.PagoDtoMapper;
import com.logistica.VisualizarEstadoPago.domain.exceptions.PagoNoEncontradoException;
import com.logistica.VisualizarEstadoPago.domain.models.Pago;
import com.logistica.VisualizarEstadoPago.domain.repositories.PagoRepository;

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
