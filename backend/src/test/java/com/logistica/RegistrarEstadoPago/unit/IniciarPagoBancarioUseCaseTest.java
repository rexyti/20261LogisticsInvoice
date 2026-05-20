package com.logistica.RegistrarEstadoPago.unit;

import com.logistica.application.registrarEstadoPago.dtos.response.IniciarPagoResponseDTO;
import com.logistica.application.registrarEstadoPago.usecases.pago.IniciarPagoBancarioUseCase;
import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;
import com.logistica.domain.registrarEstadoPago.exceptions.LiquidacionNoEncontradaException;
import com.logistica.domain.registrarEstadoPago.exceptions.PagoRechazadoBancoException;
import com.logistica.domain.registrarEstadoPago.models.RegistrarEstadoPagoPago;
import com.logistica.domain.registrarEstadoPago.models.VoucherPago;
import com.logistica.domain.registrarEstadoPago.ports.BancoGateway;
import com.logistica.domain.registrarEstadoPago.ports.LiquidacionPagoPort;
import com.logistica.domain.registrarEstadoPago.repositories.RegistrarEstadoPagoEstadoPagoRepository;
import com.logistica.domain.registrarEstadoPago.repositories.RegistrarEstadoPagoPagoRepository;
import com.logistica.domain.registrarEstadoPago.services.EstadoPagoDomainService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IniciarPagoBancarioUseCaseTest {

    @Mock private BancoGateway bancoGateway;
    @Mock private RegistrarEstadoPagoPagoRepository pagoRepository;
    @Mock private RegistrarEstadoPagoEstadoPagoRepository estadoPagoRepository;
    @Mock private LiquidacionPagoPort liquidacionPagoPort;
    @Mock private EstadoPagoDomainService estadoPagoDomainService;

    @InjectMocks
    private IniciarPagoBancarioUseCase useCase;

    private static final UUID ID_LIQUIDACION = UUID.randomUUID();
    private static final BigDecimal MONTO = new BigDecimal("1500.00");
    private static final VoucherPago VOUCHER = new VoucherPago("VCH-001", Instant.parse("2026-05-20T10:00:00Z"));

    @Test
    void ejecutar_flujoExitoso_persistePagoConVoucher() {
        when(liquidacionPagoPort.existe(ID_LIQUIDACION)).thenReturn(true);
        when(pagoRepository.findByIdLiquidacion(ID_LIQUIDACION)).thenReturn(Optional.empty());
        when(bancoGateway.registrarPago(ID_LIQUIDACION, MONTO)).thenReturn(VOUCHER);
        when(pagoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(estadoPagoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        IniciarPagoResponseDTO response = useCase.ejecutar(ID_LIQUIDACION, MONTO);

        assertThat(response.idLiquidacion()).isEqualTo(ID_LIQUIDACION);
        assertThat(response.estadoPago()).isEqualTo(RegistrarEstadoPagoEstadoPagoEnum.PAGADO);
        assertThat(response.numeroVoucher()).isEqualTo("VCH-001");
        assertThat(response.fechaProcesamiento()).isEqualTo(VOUCHER.fechaProcesamiento());
        verify(liquidacionPagoPort).marcarComoPagada(ID_LIQUIDACION);
    }

    @Test
    void ejecutar_liquidacionInexistente_lanzaExcepcion() {
        when(liquidacionPagoPort.existe(ID_LIQUIDACION)).thenReturn(false);

        assertThatThrownBy(() -> useCase.ejecutar(ID_LIQUIDACION, MONTO))
                .isInstanceOf(LiquidacionNoEncontradaException.class);

        verifyNoInteractions(bancoGateway);
        verifyNoInteractions(pagoRepository);
    }

    @Test
    void ejecutar_pagoYaEnEstadoFinal_lanzaExcepcion() {
        RegistrarEstadoPagoPago pagoFinal = pagoConEstado(RegistrarEstadoPagoEstadoPagoEnum.PAGADO);
        when(liquidacionPagoPort.existe(ID_LIQUIDACION)).thenReturn(true);
        when(pagoRepository.findByIdLiquidacion(ID_LIQUIDACION)).thenReturn(Optional.of(pagoFinal));
        when(estadoPagoDomainService.esEstadoFinal(RegistrarEstadoPagoEstadoPagoEnum.PAGADO)).thenReturn(true);

        assertThatThrownBy(() -> useCase.ejecutar(ID_LIQUIDACION, MONTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(ID_LIQUIDACION.toString());

        verifyNoInteractions(bancoGateway);
    }

    @Test
    void ejecutar_bancoRechazaPago_lanzaExcepcion() {
        when(liquidacionPagoPort.existe(ID_LIQUIDACION)).thenReturn(true);
        when(pagoRepository.findByIdLiquidacion(ID_LIQUIDACION)).thenReturn(Optional.empty());
        when(bancoGateway.registrarPago(ID_LIQUIDACION, MONTO))
                .thenThrow(new PagoRechazadoBancoException(ID_LIQUIDACION, "Fondos insuficientes"));

        assertThatThrownBy(() -> useCase.ejecutar(ID_LIQUIDACION, MONTO))
                .isInstanceOf(PagoRechazadoBancoException.class);

        verify(pagoRepository, never()).save(any());
        verify(liquidacionPagoPort, never()).marcarComoPagada(any());
    }

    @Test
    void ejecutar_pagoExistenteNoFinal_proceedeToBanco() {
        RegistrarEstadoPagoPago pagoEnProceso = pagoConEstado(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO);
        when(liquidacionPagoPort.existe(ID_LIQUIDACION)).thenReturn(true);
        when(pagoRepository.findByIdLiquidacion(ID_LIQUIDACION)).thenReturn(Optional.of(pagoEnProceso));
        when(estadoPagoDomainService.esEstadoFinal(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO)).thenReturn(false);
        when(bancoGateway.registrarPago(ID_LIQUIDACION, MONTO)).thenReturn(VOUCHER);
        when(pagoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(estadoPagoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        IniciarPagoResponseDTO response = useCase.ejecutar(ID_LIQUIDACION, MONTO);

        assertThat(response.estadoPago()).isEqualTo(RegistrarEstadoPagoEstadoPagoEnum.PAGADO);
        verify(bancoGateway).registrarPago(ID_LIQUIDACION, MONTO);
    }

    @Test
    void ejecutar_verificaPayloadEnviado_incluyeIdLiquidacionYMonto() {
        when(liquidacionPagoPort.existe(ID_LIQUIDACION)).thenReturn(true);
        when(pagoRepository.findByIdLiquidacion(ID_LIQUIDACION)).thenReturn(Optional.empty());
        when(bancoGateway.registrarPago(any(), any())).thenReturn(VOUCHER);
        when(pagoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(estadoPagoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        useCase.ejecutar(ID_LIQUIDACION, MONTO);

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<BigDecimal> montoCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        verify(bancoGateway).registrarPago(idCaptor.capture(), montoCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(ID_LIQUIDACION);
        assertThat(montoCaptor.getValue()).isEqualByComparingTo(MONTO);
    }

    private RegistrarEstadoPagoPago pagoConEstado(RegistrarEstadoPagoEstadoPagoEnum estado) {
        return new RegistrarEstadoPagoPago(UUID.randomUUID(), null, MONTO, Instant.now(),
                null, MONTO, ID_LIQUIDACION, estado, Instant.now(), 1L, null, null);
    }
}
