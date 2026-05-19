package com.logistica.application.cierreRuta.ports.in;

import com.logistica.application.cierreRuta.dtos.request.RutaCerradaEventDTO;

public interface ProcesarRutaCerradaPort {
    void ejecutar(RutaCerradaEventDTO evento);
}
