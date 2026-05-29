package com.logistica.application.registrarEstadoPago.usecases.pago;

import com.logistica.application.registrarEstadoPago.dtos.response.IniciarPagoResponseDTO;
import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;
import com.logistica.domain.registrarEstadoPago.exceptions.LiquidacionNoEncontradaException;
import com.logistica.domain.registrarEstadoPago.models.RegistrarEstadoPagoEstadoPago;
import com.logistica.domain.registrarEstadoPago.models.RegistrarEstadoPagoPago;
import com.logistica.domain.registrarEstadoPago.models.VoucherPago;
import com.logistica.domain.registrarEstadoPago.ports.BancoGateway;
import com.logistica.domain.registrarEstadoPago.ports.LiquidacionPagoPort;
import com.logistica.domain.registrarEstadoPago.repositories.RegistrarEstadoPagoEstadoPagoRepository;
import com.logistica.domain.registrarEstadoPago.repositories.RegistrarEstadoPagoPagoRepository;
import com.logistica.domain.registrarEstadoPago.services.EstadoPagoDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IniciarPagoBancarioUseCase {

    private final BancoGateway bancoGateway;
    private final RegistrarEstadoPagoPagoRepository pagoRepository;
    private final RegistrarEstadoPagoEstadoPagoRepository estadoPagoRepository;
    private final LiquidacionPagoPort liquidacionPagoPort;
    private final EstadoPagoDomainService estadoPagoDomainService;

    @Transactional
    public IniciarPagoResponseDTO ejecutar(UUID idLiquidacion, BigDecimal monto) {
        log.info("Iniciando pago bancario. id_liquidacion: {}, monto: {}", idLiquidacion, monto);

        if (!liquidacionPagoPort.existe(idLiquidacion)) {
            throw new LiquidacionNoEncontradaException("Liquidación no encontrada: " + idLiquidacion);
        }

        pagoRepository.findByIdLiquidacion(idLiquidacion).ifPresent(p -> {
            if (estadoPagoDomainService.esEstadoFinal(p.estadoActual())) {
                throw new IllegalStateException(
                        "Ya existe un pago en estado final para la liquidación: " + idLiquidacion);
            }
        });

        VoucherPago voucher = bancoGateway.registrarPago(idLiquidacion, monto);

        UUID idPago = UUID.randomUUID();
        Instant ahora = Instant.now();

        RegistrarEstadoPagoPago pago = new RegistrarEstadoPagoPago(
                idPago, null, monto, ahora, null, monto,
                idLiquidacion, RegistrarEstadoPagoEstadoPagoEnum.PAGADO, ahora, 0L,
                voucher.numeroVoucher(), voucher.fechaProcesamiento()
        );
        RegistrarEstadoPagoPago pagoPersistido = pagoRepository.save(pago);

        estadoPagoRepository.save(new RegistrarEstadoPagoEstadoPago(
                null, idPago, RegistrarEstadoPagoEstadoPagoEnum.PAGADO, ahora, ahora, 0L, null
        ));

        liquidacionPagoPort.marcarComoPagada(idLiquidacion);

        log.info("Pago bancario registrado. id_pago: {}, voucher: {}, id_liquidacion: {}",
                idPago, voucher.numeroVoucher(), idLiquidacion);

        return new IniciarPagoResponseDTO(
                pagoPersistido.idPago(),
                pagoPersistido.idLiquidacion(),
                pagoPersistido.estadoActual(),
                monto,
                pagoPersistido.numeroVoucher(),
                pagoPersistido.fechaProcesamiento()
        );
    }
}
