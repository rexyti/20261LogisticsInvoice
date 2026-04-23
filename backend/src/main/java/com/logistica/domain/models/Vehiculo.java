package com.logistica.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vehiculo {
    private Long idVehiculo;
    private Long idUsuario;
    private String tipo;
}
