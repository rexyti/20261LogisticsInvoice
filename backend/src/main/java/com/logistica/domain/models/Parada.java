package com.logistica.domain.models;

import com.logistica.domain.enums.EstadoParada;
import com.logistica.domain.enums.MotivoFalla;
import com.logistica.domain.enums.ResponsableFalla;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class Parada {

    private UUID paradaId;
    private EstadoParada estado;
    private MotivoFalla motivoFalla;
    private  ResponsableFalla responsableFalla;

    public  ResponsableFalla getResponsable(){
        return motivoFalla != null ? motivoFalla.getResponsable() : null;

    }

    public static   Parada crear( UUID paradaId, EstadoParada estado, MotivoFalla motivoFalla ){
        if(estado == EstadoParada.FALLIDA && motivoFalla == null){
            throw new IllegalArgumentException("Motivo Falla nula parada. paradaId" + paradaId);
        }
        return Parada.builder()
                .paradaId(paradaId)
                .estado(estado)
                .motivoFalla(motivoFalla)
                .build();
    }

}
