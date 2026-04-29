package com.logistica.cierreRuta.domain.services;

import com.logistica.cierreRuta.domain.enums.EstadoParada;
import com.logistica.cierreRuta.domain.enums.ResponsableFalla;
import com.logistica.cierreRuta.domain.exceptions.ParadaInvalidaException;
import com.logistica.cierreRuta.domain.models.Parada;
import com.logistica.cierreRuta.domain.models.CierreRutaRuta;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ClasificacionRutaService {

    public void clasificar(CierreRutaRuta ruta) {
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
