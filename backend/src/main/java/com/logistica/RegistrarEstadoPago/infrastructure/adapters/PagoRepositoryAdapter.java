package com.logistica.RegistrarEstadoPago.infrastructure.adapters;

import com.logistica.RegistrarEstadoPago.domain.models.Pago;
import com.logistica.RegistrarEstadoPago.domain.repositories.PagoRepository;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.entities.PagoEntity;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.repositories.PagoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PagoRepositoryAdapter implements PagoRepository {

    private final PagoJpaRepository pagoJpaRepository;
    private final PagoMapper pagoMapper;

    @Override
    public Optional<Pago> findById(UUID idPago) {
        return pagoJpaRepository.findById(idPago).map(pagoMapper::toDomain);
    }

    @Override
    public Optional<Pago> findByIdLiquidacion(UUID idLiquidacion) {
        return pagoJpaRepository.findByIdLiquidacion(idLiquidacion).stream()
                .findFirst()
                .map(pagoMapper::toDomain);
    }

    @Override
    public Pago save(Pago pago) {
        PagoEntity entity = pagoJpaRepository.findById(pago.idPago())
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
