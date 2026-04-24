package com.logistica.domain.validators;

import com.logistica.domain.enums.EstadoParada;
import com.logistica.domain.exceptions.RutaInvalidaException;
import com.logistica.domain.models.Parada;
import com.logistica.domain.models.Ruta;
import org.springframework.stereotype.Component;

@Component
public class RutaValidator {

    public void validar(Ruta ruta, int totalParadasEsperadas) {
        validarEstructura(ruta);
        validarConsistenciaParadas(ruta, totalParadasEsperadas);
        validarIntegridadParadas(ruta);
    }

    private void validarEstructura(Ruta ruta) {
        if (ruta == null) {
            throw new RutaInvalidaException("Ruta inválida no puede ser null");
        }
        if (ruta.getRutaId() == null) {
            throw  new RutaInvalidaException("Ruta sin rutaId, debe tener");
        }
        if (ruta.getParadas() == null) {
            throw new RutaInvalidaException("Ruta sin lista de paradas, debe tener");

        }
    }

    private void validarConsistenciaParadas(Ruta ruta, int totalParadasEsperadas) {
        int total = ruta.getParadas().size();

        if (total != totalParadasEsperadas) {
            throw  new RutaInvalidaException(
                    "Total de paradas no coincide. rutaId: " + ruta.getRutaId() +
            " esperadas=" + totalParadasEsperadas +
                    " recibidas=" + total
            );
        }
    }

    private void validarIntegridadParadas(Ruta ruta) {

        for (Parada parada : ruta.getParadas()) {

            if (parada == null) {
                throw new RutaInvalidaException(
                        "Parada nula en rutaId: " + ruta.getRutaId()
                );
            }

            if (parada.getEstado() == null) {
                throw new RutaInvalidaException(
                        "Parada sin estado. paradaId: " + parada.getParadaId()
                );
            }

            if (parada.getEstado() == EstadoParada.FALLIDA
                    && parada.getMotivoFalla() == null) {

                throw new RutaInvalidaException(
                        "Parada fallida sin motivo. paradaId: " + parada.getParadaId()
                );
            }
        }
    }

}
