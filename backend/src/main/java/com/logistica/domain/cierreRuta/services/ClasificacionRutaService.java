package com.logistica.domain.cierreRuta.services;

import com.logistica.domain.cierreRuta.enums.EstadoParada;
import com.logistica.domain.cierreRuta.enums.ResponsableFalla;
import com.logistica.domain.cierreRuta.exceptions.ParadaInvalidaException;
import com.logistica.domain.cierreRuta.models.Parada;
import com.logistica.domain.cierreRuta.models.RutaCerrada;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ClasificacionRutaService {

    public void clasificar(RutaCerrada ruta) {
        if (ruta == null || ruta.getParadas() == null) return;

        ruta.getParadas().forEach(this::procesarParada);
    }

    private void procesarParada(Parada parada){
        if(parada == null){
            throw new ParadaInvalidaException("Parada inválida");
        }
        if(parada.getEstado() == EstadoParada.FALLIDA){
            validarParadaFallida(parada);
        }
    }

    public void validarParadaFallida(Parada parada){
        if(parada.getMotivoFalla() == null) {
            throw new ParadaInvalidaException(
                    "Parada fallida sin motivoFalla. paradaId: " + parada.getParadaId());
        }
    }

    public long contarParadasPorResponsable(List<Parada> paradas,
                                            ResponsableFalla responsable) {
        if (paradas == null || paradas.isEmpty()) return 0;

        return paradas.stream()
                .filter(Objects::nonNull)
                .filter(p -> p.getMotivoFalla() != null)
                .filter(p -> p.getEstado() == EstadoParada.FALLIDA)
                .filter(p -> p.getMotivoFalla().getResponsable() == responsable)
                .count();
    }
}
