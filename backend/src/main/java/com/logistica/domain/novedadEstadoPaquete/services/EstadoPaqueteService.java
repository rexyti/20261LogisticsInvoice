package com.logistica.domain.novedadEstadoPaquete.services;

import com.logistica.domain.novedadEstadoPaquete.enums.NovedadEstadoPaqueteEstadoPaquete;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EstadoPaqueteService {

    public Optional<NovedadEstadoPaqueteEstadoPaquete> resolverEstado(String estadoRaw) {
        return NovedadEstadoPaqueteEstadoPaquete.fromString(estadoRaw);
    }

    public int calcularPorcentajePago(NovedadEstadoPaqueteEstadoPaquete estado) {
        return estado.getPorcentajePago();
    }
}
