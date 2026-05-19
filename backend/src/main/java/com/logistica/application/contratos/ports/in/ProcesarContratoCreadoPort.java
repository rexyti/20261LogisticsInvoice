package com.logistica.application.contratos.ports.in;

import com.logistica.application.contratos.dtos.request.ContratoCreadoEvent;

public interface ProcesarContratoCreadoPort {
    void ejecutar(ContratoCreadoEvent evento);
}
