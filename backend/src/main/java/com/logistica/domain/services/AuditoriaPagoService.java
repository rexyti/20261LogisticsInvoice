package com.logistica.domain.services;

import java.util.UUID;

public class AuditoriaPagoService {

    public void registrarIntentoAccesoNoAutorizado(UUID pagoId, UUID usuarioId) {
        // Lógica para registrar el intento de acceso no autorizado
        System.out.println("Intento de acceso no autorizado al pago " + pagoId + " por el usuario " + usuarioId);
    }
}
