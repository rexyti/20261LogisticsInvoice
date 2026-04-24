package com.logistica.domain.services;

import com.logistica.domain.enums.EstadoPaquete;

import java.util.Optional;

public class EstadoPaqueteService {

    public Optional<EstadoPaquete> mapearEstado(String estadoRecibido) {
        return EstadoPaquete.fromString(estadoRecibido);
    }

    public int calcularPorcentajePago(EstadoPaquete estado) {
        return estado.getPorcentajePago();
    }
}
