package com.logistica.VisualizarEstadoPago.application.usecases.pago;

import com.logistica.VisualizarEstadoPago.application.dtos.response.VisualizarEstadoPagoEstadoPagoResponseDTO;
import com.logistica.VisualizarEstadoPago.application.mappers.PagoDtoMapper;
import com.logistica.VisualizarEstadoPago.domain.exceptions.AccessDeniedPaymentException;
import com.logistica.VisualizarEstadoPago.domain.exceptions.VisualizarEstadoPagoPagoNoEncontradoException;
import com.logistica.VisualizarEstadoPago.domain.models.VisualizarEstadoPagoPago;
import com.logistica.VisualizarEstadoPago.domain.repositories.VisualizarEstadoPagoPagoRepository;
import com.logistica.VisualizarEstadoPago.domain.services.AuditoriaPagoService;

import java.util.UUID;

public class ConsultarEstadoPagoUseCase {

    private final VisualizarEstadoPagoPagoRepository pagoRepository;
    private final AuditoriaPagoService auditoriaPagoService;
    private final PagoDtoMapper pagoDtoMapper;

    public ConsultarEstadoPagoUseCase(VisualizarEstadoPagoPagoRepository pagoRepository, AuditoriaPagoService auditoriaPagoService) {
        this.pagoRepository = pagoRepository;
        this.auditoriaPagoService = auditoriaPagoService;
        this.pagoDtoMapper = new PagoDtoMapper();
    }

    public VisualizarEstadoPagoEstadoPagoResponseDTO ejecutar(UUID pagoId, UUID usuarioId) {
        VisualizarEstadoPagoPago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new VisualizarEstadoPagoPagoNoEncontradoException("VisualizarEstadoPagoPago no encontrado: " + pagoId));

        if (!pago.getUsuarioId().equals(usuarioId)) {
            auditoriaPagoService.registrarIntentoAccesoNoAutorizado(pagoId, usuarioId);
            throw new AccessDeniedPaymentException("Acceso denegado al pago: " + pagoId);
        }

        return pagoDtoMapper.toEstadoPagoResponseDTO(pago);
    }
}
