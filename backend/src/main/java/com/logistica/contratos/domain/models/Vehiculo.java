package com.logistica.contratos.domain.models;

import com.logistica.contratos.domain.enums.ContratosTipoVehiculo;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class Vehiculo {
    private UUID idVehiculo;
    private ContratosTipoVehiculo tipo;
}
