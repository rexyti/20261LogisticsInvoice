package com.logistica.domain.services;

import com.logistica.domain.models.Pago;
import com.logistica.domain.repositories.PagoRepository;

import java.util.UUID;

public class ProcesadorEstadoPagoService {

    private final PagoRepository pagoRepository;

    public ProcesadorEstadoPagoService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    public void procesarEstado(UUID pagoId, String estado) {
        Pago pago = pagoRepository.findById(pagoId).orElse(null);
        if (pago != null) {
            // Lógica para procesar el estado del pago
        }
    }
}
