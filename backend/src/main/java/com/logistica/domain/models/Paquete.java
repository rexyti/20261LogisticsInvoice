package com.logistica.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paquete {

    private Long idPaquete;
    private Long idRuta;
    private String estadoActual;
    private LocalDateTime updatedAt;
}
