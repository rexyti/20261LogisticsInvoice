package com.logistica.application.visualizarEstadoPago.mappers;

import com.logistica.application.visualizarEstadoPago.dtos.response.VisualizarEstadoPagoEstadoPagoResponseDTO;
import com.logistica.application.visualizarEstadoPago.dtos.response.PagoListDTO;
import com.logistica.domain.visualizarEstadoPago.models.VisualizarEstadoPagoPago;

public class PagoDtoMapper {

    public VisualizarEstadoPagoEstadoPagoResponseDTO toEstadoPagoResponseDTO(VisualizarEstadoPagoPago pago) {
        if (pago == null) {
            return null;
        }
        return new VisualizarEstadoPagoEstadoPagoResponseDTO(
                pago.getId(),
                pago.getEstado().name(),
                pago.getFecha(),
                pago.getMontoNeto(),
                null,
                pago.getLiquidacionId()
        );
    }

    public PagoListDTO toPagoListDTO(VisualizarEstadoPagoPago pago) {
        if (pago == null) {
            return null;
        }
        return new PagoListDTO(
                pago.getId(),
                pago.getLiquidacionId(),
                pago.getFecha(),
                pago.getMontoNeto(),
                pago.getEstado().name()
        );
    }
}
