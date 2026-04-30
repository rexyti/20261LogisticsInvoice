package com.logistica.VisualizarEstadoPago.infrastructure.adapters;

import com.logistica.VisualizarEstadoPago.application.dtos.response.EstadoPagoResponseDTO;
import com.logistica.VisualizarEstadoPago.domain.models.Pago;
import com.logistica.VisualizarEstadoPago.infrastructure.persistence.entities.PagoEntity;
import org.springframework.stereotype.Component;

@Component
public class PagoMapper {

    public EstadoPagoResponseDTO toEstadoPagoResponseDTO(Pago pago) {
        if (pago == null) {
            return null;
        }
        return new EstadoPagoResponseDTO(
                pago.getId(),
                pago.getEstado().name(),
                pago.getFecha(),
                pago.getMontoNeto(),
                null, // Motivo de rechazo no está en el modelo de dominio
                pago.getLiquidacionId()
        );
    }

    public Pago toDomain(PagoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Pago(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getMontoBase(),
                entity.getFecha(),
                entity.getPenalidadId(),
                entity.getMontoNeto(),
                entity.getLiquidacionId(),
                entity.getEstado()
        );
    }

    public PagoEntity toEntity(Pago domain) {
        if (domain == null) {
            return null;
        }
        return new PagoEntity(
                domain.getId(),
                domain.getUsuarioId(),
                domain.getMontoBase(),
                domain.getFecha(),
                domain.getPenalidadId(),
                domain.getMontoNeto(),
                domain.getLiquidacionId(),
                domain.getEstado()
        );
    }
}
