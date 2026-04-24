package com.logistica.infrastructure.adapters;

import com.logistica.domain.models.Ajuste;
import com.logistica.domain.models.Liquidacion;
import com.logistica.domain.models.Ruta;
import com.logistica.infrastructure.persistence.entities.AjusteEntity;
import com.logistica.infrastructure.persistence.entities.LiquidacionEntity;
import com.logistica.infrastructure.persistence.entities.RutaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LiquidacionMapper {

    public Liquidacion toDomain(LiquidacionEntity entity) {
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

    private Ruta toRutaDomain(RutaEntity entity) {
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
                        .tipo(e.getTipo())
                        .monto(e.getMonto())
                        .razon(e.getRazon())
                        .build())
                .toList();
    }
}
