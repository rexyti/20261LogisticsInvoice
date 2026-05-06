package com.logistica.infrastructure.visualizarEstadoPago.adapters;

import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;
import com.logistica.domain.visualizarEstadoPago.enums.VisualizarEstadoPagoEstadoPagoEnum;
import com.logistica.domain.visualizarEstadoPago.models.VisualizarEstadoPagoPago;
import com.logistica.infrastructure.registrarEstadoPago.persistence.entities.RegistrarEstadoPagoPagoEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;

@Component
public class PagoPersistenceMapper {

    public VisualizarEstadoPagoPago toDomain(RegistrarEstadoPagoPagoEntity entity) {
        if (entity == null) return null;

        VisualizarEstadoPagoEstadoPagoEnum estado = null;
        if (entity.getEstadoActual() != null) {
            try {
                estado = VisualizarEstadoPagoEstadoPagoEnum.valueOf(entity.getEstadoActual().name());
            } catch (IllegalArgumentException ignored) {
            }
        }

        return new VisualizarEstadoPagoPago(
                entity.getIdPago(),
                entity.getIdUsuario(),
                entity.getMontoBase(),
                entity.getFecha() != null
                        ? entity.getFecha().atZone(ZoneId.systemDefault()).toLocalDateTime()
                        : null,
                entity.getIdPenalidad(),
                entity.getMontoNeto(),
                entity.getIdLiquidacion(),
                estado
        );
    }

    public RegistrarEstadoPagoPagoEntity toEntity(VisualizarEstadoPagoPago domain) {
        if (domain == null) return null;

        RegistrarEstadoPagoEstadoPagoEnum estadoActual = null;
        if (domain.getEstado() != null) {
            estadoActual = RegistrarEstadoPagoEstadoPagoEnum.valueOf(domain.getEstado().name());
        }

        return RegistrarEstadoPagoPagoEntity.builder()
                .idPago(domain.getId())
                .idUsuario(domain.getUsuarioId())
                .montoBase(domain.getMontoBase())
                .fecha(domain.getFecha() != null
                        ? domain.getFecha().atZone(ZoneId.systemDefault()).toInstant()
                        : null)
                .idPenalidad(domain.getPenalidadId())
                .montoNeto(domain.getMontoNeto())
                .idLiquidacion(domain.getLiquidacionId())
                .estadoActual(estadoActual)
                .fechaUltimaActualizacion(Instant.now())
                .ultimaSecuenciaProcesada(0L)
                .build();
    }
}
