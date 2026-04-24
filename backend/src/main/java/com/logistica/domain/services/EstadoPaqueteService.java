package com.logistica.domain.services;

import com.logistica.domain.enums.EstadoPaquete;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EstadoPaqueteService {

    public Optional<EstadoPaquete> resolverEstado(String estadoRaw) {
        return EstadoPaquete.fromString(estadoRaw);
    }

    public int calcularPorcentajePago(EstadoPaquete estado) {
        return estado.getPorcentajePago();
    }
}
