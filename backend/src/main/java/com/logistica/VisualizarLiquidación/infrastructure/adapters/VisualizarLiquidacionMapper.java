package com.logistica.VisualizarLiquidación.infrastructure.adapters;

import com.logistica.VisualizarLiquidación.domain.models.Ajuste;
import com.logistica.VisualizarLiquidación.domain.models.Liquidacion;
import com.logistica.VisualizarLiquidación.domain.models.Ruta;
import com.logistica.VisualizarLiquidación.infrastructure.persistence.entities.VisualizarLiquidacionAjusteEntity;
import com.logistica.VisualizarLiquidación.infrastructure.persistence.entities.VisualizarLiquidacionEntity;
import com.logistica.VisualizarLiquidación.infrastructure.persistence.entities.VisualizarLiquidacionRutaEntity;
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

    private List<Ajuste> toAjustesDomain(List<VisualizarLiquidacionAjusteEntity> entities) {
        if (entities == null) return List.of();
        return entities.stream()
                .map(e -> Ajuste.builder()
                        .id(e.getId())
                        .tipo(e.getTipo())
                        .monto(e.getMonto())
                        .razon(e.getRazon())
                        .build())
                .toList();
    }
}
