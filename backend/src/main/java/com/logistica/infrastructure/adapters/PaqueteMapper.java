package com.logistica.infrastructure.adapters;

import com.logistica.domain.models.HistorialEstado;
import com.logistica.domain.models.LogSincronizacion;
import com.logistica.domain.models.Paquete;
import com.logistica.infrastructure.persistence.entities.HistorialEstadoEntity;
import com.logistica.infrastructure.persistence.entities.LogSincronizacionEntity;
import com.logistica.infrastructure.persistence.entities.PaqueteEntity;
import org.springframework.stereotype.Component;

@Component
public class PaqueteMapper {

    public Paquete toDomain(PaqueteEntity entity) {
        return new Paquete(
                entity.getIdPaquete(),
                entity.getIdRuta(),
                entity.getEstadoActual(),
                entity.getVersion()
        );
    }

    public PaqueteEntity toEntity(Paquete domain) {
        return new PaqueteEntity(
                domain.idPaquete(),
                domain.idRuta(),
                domain.estadoActual(),
                domain.version()
        );
    }

    public HistorialEstado toDomain(HistorialEstadoEntity entity) {
        return new HistorialEstado(entity.getId(), entity.getIdPaquete(), entity.getEstado(), entity.getFecha());
    }

    public HistorialEstadoEntity toEntity(HistorialEstado domain) {
        return new HistorialEstadoEntity(domain.id(), domain.idPaquete(), domain.estado(), domain.fecha());
    }

    public LogSincronizacion toDomain(LogSincronizacionEntity entity) {
        return new LogSincronizacion(entity.getId(), entity.getIdPaquete(),
                entity.getCodigoRespuestaHTTP(), entity.getJsonRecibido(), entity.getTimestamp());
    }

    public LogSincronizacionEntity toEntity(LogSincronizacion domain) {
        return new LogSincronizacionEntity(domain.id(), domain.idPaquete(),
                domain.codigoRespuestaHTTP(), domain.jsonRecibido(), domain.timestamp());
    }
}

