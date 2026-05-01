package com.logistica.RegistrarEstadoPago.infrastructure.adapters;

import com.logistica.RegistrarEstadoPago.domain.models.RegistrarEstadoPagoEstadoPago;
import com.logistica.RegistrarEstadoPago.domain.models.RegistrarEstadoPagoEventoTransaccion;
import com.logistica.RegistrarEstadoPago.domain.models.LiquidacionReferencia;
import com.logistica.RegistrarEstadoPago.domain.models.RegistrarEstadoPagoPago;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.entities.RegistrarEstadoPagoEstadoPagoEntity;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.entities.EventoTransaccionEntity;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.entities.LiquidacionReferenciaEntity;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.entities.RegistrarEstadoPagoPagoEntity;
import org.springframework.stereotype.Component;

@Component
public class PagoMapper {

    public RegistrarEstadoPagoPago toDomain(RegistrarEstadoPagoPagoEntity entity) {
        return new RegistrarEstadoPagoPago(
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

    public RegistrarEstadoPagoPagoEntity toEntity(RegistrarEstadoPagoPago domain) {
        return RegistrarEstadoPagoPagoEntity.builder()
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

    public RegistrarEstadoPagoEstadoPago toDomain(RegistrarEstadoPagoEstadoPagoEntity entity) {
        return new RegistrarEstadoPagoEstadoPago(
                entity.getIdEstadoPago(),
                entity.getIdPago(),
                entity.getEstado(),
                entity.getFechaRegistro(),
                entity.getFechaEventoBanco(),
                entity.getSecuenciaEvento(),
                entity.getIdEventoTransaccion()
        );
    }

    public RegistrarEstadoPagoEstadoPagoEntity toEntity(RegistrarEstadoPagoEstadoPago domain) {
        return RegistrarEstadoPagoEstadoPagoEntity.builder()
                .idEstadoPago(domain.idEstadoPago())
                .idPago(domain.idPago())
                .estado(domain.estado())
                .fechaRegistro(domain.fechaRegistro())
                .fechaEventoBanco(domain.fechaEventoBanco())
                .secuenciaEvento(domain.secuenciaEvento())
                .idEventoTransaccion(domain.idEventoTransaccion())
                .build();
    }

    public RegistrarEstadoPagoEventoTransaccion toDomain(EventoTransaccionEntity entity) {
        return new RegistrarEstadoPagoEventoTransaccion(
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

    public EventoTransaccionEntity toEntity(RegistrarEstadoPagoEventoTransaccion domain) {
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
