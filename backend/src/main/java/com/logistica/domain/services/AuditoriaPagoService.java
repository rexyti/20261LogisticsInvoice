package com.logistica.domain.services;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuditoriaPagoService {

    public void registrarIntentoAccesoNoAutorizado(UUID pagoId, UUID usuarioId) {
        // Lógica para registrar el intento de acceso no autorizado
        System.out.println("Intento de acceso no autorizado al pago " + pagoId + " por el usuario " + usuarioId);
    }
}
