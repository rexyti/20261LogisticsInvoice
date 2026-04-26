package com.logistica.infrastructure.adapters;

import com.logistica.domain.models.EstadoPago;
import com.logistica.domain.models.EventoTransaccion;
import com.logistica.domain.models.LiquidacionReferencia;
import com.logistica.domain.models.Pago;
import com.logistica.infrastructure.persistence.entities.EstadoPagoEntity;
import com.logistica.infrastructure.persistence.entities.EventoTransaccionEntity;
import com.logistica.infrastructure.persistence.entities.LiquidacionReferenciaEntity;
import com.logistica.infrastructure.persistence.entities.PagoEntity;
import org.springframework.stereotype.Component;

@Component
public class PagoMapper {

    public Pago toDomain(PagoEntity entity) {
        return new Pago(
                entity.getIdPago(),
                entity.getIdUsuario(),
                entity.getMontoBase(),
                entity.getFecha(),
                entity.getIdPenalidad(),
                entity.getMontoNeto(),
                entity.getIdLiquidacion(),
                entity.getEstadoActual(),
                entity.getFechaUltimaActualizacion(),
                entity.getUltimaSecuenciaProcesada()
        );
    }

    public PagoEntity toEntity(Pago domain) {
        return PagoEntity.builder()
                .idPago(domain.idPago())
                .idUsuario(domain.idUsuario())
                .montoBase(domain.montoBase())
                .fecha(domain.fecha())
                .idPenalidad(domain.idPenalidad())
                .montoNeto(domain.montoNeto())
                .idLiquidacion(domain.idLiquidacion())
                .estadoActual(domain.estadoActual())
                .fechaUltimaActualizacion(domain.fechaUltimaActualizacion())
                .ultimaSecuenciaProcesada(domain.ultimaSecuenciaProcesada())
                .build();
    }

    public EstadoPago toDomain(EstadoPagoEntity entity) {
        return new EstadoPago(
                entity.getIdEstadoPago(),
                entity.getIdPago(),
                entity.getEstado(),
                entity.getFechaRegistro(),
                entity.getFechaEventoBanco(),
                entity.getSecuenciaEvento(),
                entity.getIdEventoTransaccion()
        );
    }

    public EstadoPagoEntity toEntity(EstadoPago domain) {
        return EstadoPagoEntity.builder()
                .idEstadoPago(domain.idEstadoPago())
                .idPago(domain.idPago())
                .estado(domain.estado())
                .fechaRegistro(domain.fechaRegistro())
                .fechaEventoBanco(domain.fechaEventoBanco())
                .secuenciaEvento(domain.secuenciaEvento())
                .idEventoTransaccion(domain.idEventoTransaccion())
                .build();
    }

    public EventoTransaccion toDomain(EventoTransaccionEntity entity) {
        return new EventoTransaccion(
                entity.getIdEvento(),
                entity.getIdTransaccionBanco(),
                entity.getIdPago(),
                entity.getIdLiquidacion(),
                entity.getPayloadRecibido(),
                entity.getFechaRecepcion(),
                entity.getFechaEventoBanco(),
                entity.getSecuencia(),
                entity.getEstadoSolicitado(),
                entity.getEstadoProcesamiento(),
                entity.getMensajeError(),
                entity.isProcesado()
        );
    }

    public EventoTransaccionEntity toEntity(EventoTransaccion domain) {
        return EventoTransaccionEntity.builder()
                .idEvento(domain.idEvento())
                .idTransaccionBanco(domain.idTransaccionBanco())
                .idPago(domain.idPago())
                .idLiquidacion(domain.idLiquidacion())
                .payloadRecibido(domain.payloadRecibido())
                .fechaRecepcion(domain.fechaRecepcion())
                .fechaEventoBanco(domain.fechaEventoBanco())
                .secuencia(domain.secuencia())
                .estadoSolicitado(domain.estadoSolicitado())
                .estadoProcesamiento(domain.estadoProcesamiento())
                .mensajeError(domain.mensajeError())
                .procesado(domain.procesado())
                .build();
    }

    public LiquidacionReferencia toDomain(LiquidacionReferenciaEntity entity) {
        return new LiquidacionReferencia(entity.getIdLiquidacion());
    }

    public LiquidacionReferenciaEntity toEntity(LiquidacionReferencia domain) {
        return new LiquidacionReferenciaEntity(domain.idLiquidacion());
    }
}
