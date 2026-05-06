package com.logistica.application.visualizarEstadoPago.usecases.pago;

import com.logistica.application.visualizarEstadoPago.dtos.response.VisualizarEstadoPagoEstadoPagoResponseDTO;
import com.logistica.application.visualizarEstadoPago.mappers.PagoDtoMapper;
import com.logistica.domain.visualizarEstadoPago.exceptions.AccessDeniedPaymentException;
import com.logistica.domain.visualizarEstadoPago.exceptions.VisualizarEstadoPagoPagoNoEncontradoException;
import com.logistica.domain.visualizarEstadoPago.models.VisualizarEstadoPagoPago;
import com.logistica.domain.visualizarEstadoPago.repositories.VisualizarEstadoPagoPagoRepository;
import com.logistica.domain.visualizarEstadoPago.services.AuditoriaPagoService;

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
