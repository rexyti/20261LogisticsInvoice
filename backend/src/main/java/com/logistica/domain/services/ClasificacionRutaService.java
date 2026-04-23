package com.logistica.domain.services;

import com.logistica.domain.enums.EstadoParada;
import com.logistica.domain.enums.ResponsableFalla;
import com.logistica.domain.models.Parada;
import com.logistica.domain.models.Ruta;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClasificacionRutaService {

    public void clasificar(Ruta ruta) {
        if (ruta.getParadas() != null) {
            clasificarParadas(ruta.getParadas());
        }
    }

    public void clasificarParadas(List<Parada> paradas){
        paradas.stream()
                .filter(parada -> parada.getEstado() == EstadoParada.FALLIDA)
                .forEach(this::validarParadaFallida);
    }

    public void validarParadaFallida(Parada parada){
        if(parada.getMotivoFalla() == null) {
            throw new IllegalStateException(
                    "Parada fallida sin motivoFalla. paradaId" + parada.getParadaId());
        }
    }

    public long contarParadasPorResponsable(List<Parada> paradas,
                                            ResponsableFalla responsable) {
        return paradas.stream()
                .filter(p -> p.getMotivoFalla() != null)
                .filter(p -> p.getResponsable() == responsable)
                .count();
    }
}
