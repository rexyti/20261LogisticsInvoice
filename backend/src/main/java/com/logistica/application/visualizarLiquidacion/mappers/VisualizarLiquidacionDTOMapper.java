package com.logistica.application.visualizarLiquidacion.mappers;

import com.logistica.application.visualizarLiquidacion.dtos.response.VisualizarLiquidacionAjusteDTO;
import com.logistica.application.visualizarLiquidacion.dtos.response.VisualizarLiquidacionDetalleDTO;
import com.logistica.application.visualizarLiquidacion.dtos.response.VisualizarLiquidacionListItemDTO;
import com.logistica.domain.visualizarLiquidacion.models.Ajuste;
import com.logistica.domain.visualizarLiquidacion.models.Liquidacion;
import com.logistica.domain.visualizarLiquidacion.models.Ruta;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VisualizarLiquidacionDTOMapper {

    public VisualizarLiquidacionListItemDTO toListItem(Liquidacion liq) {
        Ruta ruta = liq.getRuta();
        return new VisualizarLiquidacionListItemDTO(
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

    public VisualizarLiquidacionDetalleDTO toDetalle(Liquidacion liq) {
        Ruta ruta = liq.getRuta();
        return new VisualizarLiquidacionDetalleDTO(
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

    private List<VisualizarLiquidacionAjusteDTO> toAjustesDTO(List<Ajuste> ajustes) {
        if (ajustes == null) return List.of();
        return ajustes.stream()
                .map(a -> new VisualizarLiquidacionAjusteDTO(a.getId(), a.getTipo(), a.getMonto(), a.getRazon()))
                .toList();
    }
}
