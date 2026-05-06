package com.logistica.domain.visualizarEstadoPago.services;

import com.logistica.domain.visualizarEstadoPago.models.VisualizarEstadoPagoPago;
import com.logistica.domain.visualizarEstadoPago.repositories.VisualizarEstadoPagoPagoRepository;

import java.util.UUID;

public class ProcesadorEstadoPagoService {

    private final VisualizarEstadoPagoPagoRepository pagoRepository;

    public ProcesadorEstadoPagoService(VisualizarEstadoPagoPagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    public void procesarEstado(UUID pagoId, String estado) {
        VisualizarEstadoPagoPago pago = pagoRepository.findById(pagoId).orElse(null);
        if (pago != null) {
            // Lógica para procesar el estado del pago
        }
    }
}
