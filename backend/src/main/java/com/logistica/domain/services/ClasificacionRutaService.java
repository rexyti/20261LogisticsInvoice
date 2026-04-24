package com.logistica.domain.services;

import com.logistica.domain.enums.EstadoParada;
import com.logistica.domain.enums.ResponsableFalla;
import com.logistica.domain.exceptions.ParadaInvalidaException;
import com.logistica.domain.models.Parada;
import com.logistica.domain.models.Ruta;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClasificacionRutaService {

    public void clasificar(Ruta ruta) {
        if (ruta == null || ruta.getParadas().isEmpty()) return;

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
                .filter(p -> p != null && p.getMotivoFalla() != null)
                .filter(p -> p.getEstado() == EstadoParada.FALLIDA)
                .filter(p -> p.getMotivoFalla().getResponsable() == responsable)
                .count();
    }
}
