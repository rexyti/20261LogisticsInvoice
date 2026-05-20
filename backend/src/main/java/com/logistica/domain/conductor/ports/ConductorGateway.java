package com.logistica.domain.conductor.ports;

import com.logistica.domain.conductor.models.Conductor;

import java.util.UUID;

public interface ConductorGateway {
    Conductor consultar(UUID conductorId);
}
