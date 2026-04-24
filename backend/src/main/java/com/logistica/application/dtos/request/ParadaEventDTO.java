package com.logistica.application.dtos.request;


import com.logistica.domain.enums.EstadoParada;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ParadaEventDTO {

    @NotNull

    private UUID paradaId;

    @NotNull
    private EstadoParada estado;


    private String motivoNoEntrega;

    @AssertTrue(message = "Una parada FALLIDA debe tener motivoNoEntrega")
    private boolean isMotivoValido() {
        return estado != EstadoParada.FALLIDA ||
                (motivoNoEntrega != null && !motivoNoEntrega.isBlank());
    }
}