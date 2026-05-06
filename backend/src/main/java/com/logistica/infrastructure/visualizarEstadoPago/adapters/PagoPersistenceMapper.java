package com.logistica.infrastructure.visualizarEstadoPago.adapters;

import com.logistica.domain.visualizarEstadoPago.models.VisualizarEstadoPagoPago;
import com.logistica.infrastructure.visualizarEstadoPago.persistence.entities.VisualizarEstadoPagoPagoEntity;
import org.springframework.stereotype.Component;

@Component
public class PagoPersistenceMapper {

    public VisualizarEstadoPagoPago toDomain(VisualizarEstadoPagoPagoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new VisualizarEstadoPagoPago(
                entity.getIdPago(),
                entity.getUsuarioId(),
                entity.getMontoBase(),
                entity.getFecha(),
                entity.getPenalidadId(),
                entity.getMontoNeto(),
                entity.getLiquidacionId(),
                entity.getEstado()
        );
    }

    public VisualizarEstadoPagoPagoEntity toEntity(VisualizarEstadoPagoPago domain) {
        if (domain == null) {
            return null;
        }
        return new VisualizarEstadoPagoPagoEntity(
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
