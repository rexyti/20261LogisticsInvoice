package com.logistica.application.novedadEstadoPaquete.ports.in;

import com.logistica.application.novedadEstadoPaquete.dtos.request.NovedadEstadoPaqueteEvent;

public interface ProcesarEstadoPaquetePort {
    void ejecutar(NovedadEstadoPaqueteEvent evento);
}
