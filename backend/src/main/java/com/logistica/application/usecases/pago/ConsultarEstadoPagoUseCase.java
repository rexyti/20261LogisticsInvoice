package com.logistica.application.usecases.pago;

import com.logistica.application.dtos.response.EstadoPagoResponseDTO;
import com.logistica.domain.exceptions.PagoNoEncontradoException;
import com.logistica.domain.models.Pago;
import com.logistica.domain.repositories.PagoRepository;
import com.logistica.infrastructure.adapters.PagoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsultarEstadoPagoUseCase {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private PagoMapper pagoMapper;

    public EstadoPagoResponseDTO ejecutar(UUID pagoId) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new PagoNoEncontradoException("Pago no encontrado"));
        return pagoMapper.toEstadoPagoResponseDTO(pago);
    }
}
