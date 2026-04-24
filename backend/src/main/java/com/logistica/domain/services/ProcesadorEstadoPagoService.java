package com.logistica.domain.services;

import com.logistica.domain.models.Pago;
import com.logistica.domain.repositories.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProcesadorEstadoPagoService {

    @Autowired
    private PagoRepository pagoRepository;

    public void procesarEstado(UUID pagoId, String estado) {
        Pago pago = pagoRepository.findById(pagoId).orElse(null);
        if (pago != null) {
            // Lógica para procesar el estado del pago
        }
    }
}
