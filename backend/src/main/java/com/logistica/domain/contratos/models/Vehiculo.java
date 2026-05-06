package com.logistica.domain.contratos.models;

import com.logistica.domain.shared.enums.TipoVehiculo;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class Vehiculo {
    private UUID idVehiculo;
    private TipoVehiculo tipo;
}
