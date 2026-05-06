package com.logistica.infrastructure.visualizarLiquidacion.adapters;

import com.logistica.domain.visualizarLiquidacion.models.Ajuste;
import com.logistica.domain.visualizarLiquidacion.models.Liquidacion;
import com.logistica.domain.visualizarLiquidacion.models.Ruta;
import com.logistica.infrastructure.liquidacion.persistence.entities.AjusteEntity;
import com.logistica.infrastructure.visualizarLiquidacion.persistence.entities.VisualizarLiquidacionEntity;
import com.logistica.infrastructure.visualizarLiquidacion.persistence.entities.VisualizarLiquidacionRutaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VisualizarLiquidacionMapper {

    public Liquidacion toDomain(VisualizarLiquidacionEntity entity) {
        return Liquidacion.builder()
                .id(entity.getId())
                .idRuta(entity.getRuta() != null ? entity.getRuta().getId() : null)
                .idContrato(entity.getIdContrato())
                .estadoLiquidacion(entity.getEstadoLiquidacion())
                .montoBruto(entity.getMontoBruto())
                .montoNeto(entity.getMontoNeto())
                .fechaCalculo(entity.getFechaCalculo())
                .usuarioId(entity.getUsuarioId())
                .ruta(toRutaDomain(entity.getRuta()))
                .ajustes(toAjustesDomain(entity.getAjustes()))
                .build();
    }

    private Ruta toRutaDomain(VisualizarLiquidacionRutaEntity entity) {
        if (entity == null) return null;
        return Ruta.builder()
                .id(entity.getId())
                .fechaInicio(entity.getFechaInicio())
                .fechaCierre(entity.getFechaCierre())
                .tipoVehiculo(entity.getTipoVehiculo())
                .precioParada(entity.getPrecioParada())
                .numeroParadas(entity.getNumeroParadas())
                .build();
    }

    private List<Ajuste> toAjustesDomain(List<AjusteEntity> entities) {
        if (entities == null) return List.of();
        return entities.stream()
                .map(e -> Ajuste.builder()
                        .id(e.getId())
                        .tipo(e.getTipo() != null ? e.getTipo().name() : null)
                        .monto(e.getMonto())
                        .razon(e.getMotivo())
                        .build())
                .toList();
    }
}
