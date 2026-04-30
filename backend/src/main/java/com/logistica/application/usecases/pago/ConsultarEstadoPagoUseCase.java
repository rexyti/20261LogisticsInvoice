package com.logistica.application.usecases.pago;

import com.logistica.application.dtos.response.EstadoPagoResponseDTO;
import com.logistica.application.mappers.PagoDtoMapper;
import com.logistica.domain.exceptions.AccessDeniedPaymentException;
import com.logistica.domain.exceptions.PagoNoEncontradoException;
import com.logistica.domain.models.Pago;
import com.logistica.domain.repositories.PagoRepository;
import com.logistica.domain.services.AuditoriaPagoService;

import java.util.UUID;

public class ConsultarEstadoPagoUseCase {

    private final PagoRepository pagoRepository;
    private final AuditoriaPagoService auditoriaPagoService;
    private final PagoDtoMapper pagoDtoMapper;

    public ConsultarEstadoPagoUseCase(PagoRepository pagoRepository, AuditoriaPagoService auditoriaPagoService) {
        this.pagoRepository = pagoRepository;
        this.auditoriaPagoService = auditoriaPagoService;
        this.pagoDtoMapper = new PagoDtoMapper();
    }

    public EstadoPagoResponseDTO ejecutar(UUID pagoId, UUID usuarioId) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new PagoNoEncontradoException("Pago no encontrado: " + pagoId));

        if (!pago.getUsuarioId().equals(usuarioId)) {
            auditoriaPagoService.registrarIntentoAccesoNoAutorizado(pagoId, usuarioId);
            throw new AccessDeniedPaymentException("Acceso denegado al pago: " + pagoId);
        }

        return pagoDtoMapper.toEstadoPagoResponseDTO(pago);
    }
}
