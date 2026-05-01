package com.logistica.RegistrarEstadoPago.infrastructure.adapters;

import com.logistica.RegistrarEstadoPago.domain.models.RegistrarEstadoPagoPago;
import com.logistica.RegistrarEstadoPago.domain.repositories.RegistrarEstadoPagoPagoRepository;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.entities.RegistrarEstadoPagoPagoEntity;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.repositories.RegistrarEstadoPagoPagoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PagoRepositoryAdapter implements RegistrarEstadoPagoPagoRepository {

    private final RegistrarEstadoPagoPagoJpaRepository pagoJpaRepository;
    private final PagoMapper pagoMapper;

    @Override
    public Optional<RegistrarEstadoPagoPago> findById(UUID idPago) {
        return pagoJpaRepository.findById(idPago).map(pagoMapper::toDomain);
    }

    @Override
    public Optional<RegistrarEstadoPagoPago> findByIdLiquidacion(UUID idLiquidacion) {
        return pagoJpaRepository.findByIdLiquidacion(idLiquidacion).stream()
                .findFirst()
                .map(pagoMapper::toDomain);
    }

    @Override
    public RegistrarEstadoPagoPago save(RegistrarEstadoPagoPago pago) {
        RegistrarEstadoPagoPagoEntity entity = pagoJpaRepository.findById(pago.idPago())
                .map(existing -> {
                    existing.setIdUsuario(pago.idUsuario());
                    existing.setMontoBase(pago.montoBase());
                    existing.setFecha(pago.fecha());
                    existing.setIdPenalidad(pago.idPenalidad());
                    existing.setMontoNeto(pago.montoNeto());
                    existing.setIdLiquidacion(pago.idLiquidacion());
                    existing.setEstadoActual(pago.estadoActual());
                    existing.setFechaUltimaActualizacion(pago.fechaUltimaActualizacion());
                    existing.setUltimaSecuenciaProcesada(pago.ultimaSecuenciaProcesada());
                    return existing;
                })
                .orElse(pagoMapper.toEntity(pago));
        return pagoMapper.toDomain(pagoJpaRepository.save(entity));
    }
}
