package com.logistica.VisualizarLiquidación.application.mappers;

import com.logistica.VisualizarLiquidación.application.dtos.response.AjusteLiquidacionDTO;
import com.logistica.VisualizarLiquidación.application.dtos.response.LiquidacionDetalleDTO;
import com.logistica.VisualizarLiquidación.application.dtos.response.LiquidacionListItemDTO;
import com.logistica.VisualizarLiquidación.domain.models.Ajuste;
import com.logistica.VisualizarLiquidación.domain.models.Liquidacion;
import com.logistica.VisualizarLiquidación.domain.models.Ruta;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LiquidacionDTOMapper {

    public LiquidacionListItemDTO toListItem(Liquidacion liq) {
        Ruta ruta = liq.getRuta();
        return new LiquidacionListItemDTO(
                liq.getId(),
                liq.getIdRuta(),
                ruta != null ? ruta.getFechaInicio() : null,
                ruta != null ? ruta.getFechaCierre() : null,
                ruta != null ? ruta.getTipoVehiculo() : null,
                ruta != null ? ruta.getPrecioParada() : null,
                ruta != null ? ruta.getNumeroParadas() : null,
                liq.getMontoBruto(),
                liq.getMontoNeto(),
                liq.getEstadoLiquidacion() != null ? liq.getEstadoLiquidacion().name() : null,
                liq.getFechaCalculo(),
                toAjustesDTO(liq.getAjustes())
        );
    }

    public LiquidacionDetalleDTO toDetalle(Liquidacion liq) {
        Ruta ruta = liq.getRuta();
        return new LiquidacionDetalleDTO(
                liq.getId(),
                liq.getIdContrato(),
                liq.getIdRuta(),
                ruta != null ? ruta.getFechaInicio() : null,
                ruta != null ? ruta.getFechaCierre() : null,
                ruta != null ? ruta.getTipoVehiculo() : null,
                ruta != null ? ruta.getPrecioParada() : null,
                ruta != null ? ruta.getNumeroParadas() : null,
                liq.getMontoBruto(),
                liq.getMontoNeto(),
                liq.getEstadoLiquidacion() != null ? liq.getEstadoLiquidacion().name() : null,
                liq.getFechaCalculo(),
                liq.getUsuarioId(),
                toAjustesDTO(liq.getAjustes())
        );
    }

    private List<AjusteLiquidacionDTO> toAjustesDTO(List<Ajuste> ajustes) {
        if (ajustes == null) return List.of();
        return ajustes.stream()
                .map(a -> new AjusteLiquidacionDTO(a.getId(), a.getTipo(), a.getMonto(), a.getRazon()))
                .toList();
    }
}