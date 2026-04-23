package com.logistica.domain.validators;

import com.logistica.domain.enums.EstadoParada;
import com.logistica.domain.exceptions.DomainException;
import com.logistica.domain.models.Parada;
import com.logistica.domain.models.Ruta;
import org.springframework.stereotype.Component;

@Component
public class RutaValidator {

    public void validar(Ruta ruta, int totalParadasEsperadas) {
        if (ruta.getParadas().size() != totalParadasEsperadas) {
            throw new DomainException(
                "Total de paradas no coincide: esperadas=" + totalParadasEsperadas
                + " recibidas=" + ruta.getParadas().size()
            );
        }
        for (Parada parada : ruta.getParadas()) {
            if (EstadoParada.FALLIDA.equals(parada.getEstado()) && parada.getMotivoFalla() == null) {
                throw new DomainException(
                    "Parada FALLIDA sin motivo_no_entrega: paradaId=" + parada.getParadaId()
                );
            }
        }
    }
}
