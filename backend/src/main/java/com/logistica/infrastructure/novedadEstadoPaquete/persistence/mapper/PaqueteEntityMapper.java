package com.logistica.infrastructure.novedadEstadoPaquete.persistence.mapper;

import com.logistica.domain.novedadEstadoPaquete.models.HistorialEstado;
import com.logistica.domain.novedadEstadoPaquete.models.LogSincronizacion;
import com.logistica.domain.novedadEstadoPaquete.models.NovedadEstadoPaquetePaquete;
import com.logistica.infrastructure.novedadEstadoPaquete.persistence.entities.HistorialEstadoEntity;
import com.logistica.infrastructure.novedadEstadoPaquete.persistence.entities.LogSincronizacionEntity;
import com.logistica.infrastructure.novedadEstadoPaquete.persistence.entities.PaqueteEntity;
import org.springframework.stereotype.Component;

@Component
public class PaqueteEntityMapper {

    public NovedadEstadoPaquetePaquete toDomain(PaqueteEntity entity) {
        return new NovedadEstadoPaquetePaquete(
                entity.getIdPaquete(),
                entity.getIdRuta(),
                entity.getEstadoActual(),
                entity.getUpdatedAt()
        );
    }

    public PaqueteEntity toEntity(NovedadEstadoPaquetePaquete domain) {
        return new PaqueteEntity(
                domain.getIdPaquete(),
                domain.getIdRuta(),
                domain.getEstadoActual(),
                domain.getUpdatedAt()
        );
    }

    public HistorialEstado toDomain(HistorialEstadoEntity entity) {
        return new HistorialEstado(
                entity.getId(),
                entity.getIdPaquete(),
                entity.getEstado(),
                entity.getFecha()
        );
    }

    public HistorialEstadoEntity toEntity(HistorialEstado domain) {
        return new HistorialEstadoEntity(
                domain.getId(),
                domain.getIdPaquete(),
                domain.getEstado(),
                domain.getFecha()
        );
    }

    public LogSincronizacion toDomain(LogSincronizacionEntity entity) {
        return new LogSincronizacion(
                entity.getId(),
                entity.getIdPaquete(),
                entity.getCodigoRespuestaHTTP(),
                entity.getJsonRecibido(),
                entity.getCreatedAt()
        );
    }

    public LogSincronizacionEntity toEntity(LogSincronizacion domain) {
        return new LogSincronizacionEntity(
                domain.getId(),
                domain.getIdPaquete(),
                domain.getCodigoRespuestaHTTP(),
                domain.getJsonRecibido(),
                domain.getCreatedAt()
        );
    }
}
