package com.logistica.application.usecases.pago;

import com.logistica.application.dtos.response.EstadoPagoResponseDTO;
import com.logistica.domain.enums.EstadoPagoEnum;
import com.logistica.domain.exceptions.AccessDeniedPaymentException;
import com.logistica.domain.exceptions.PagoNoEncontradoException;
import com.logistica.domain.models.Pago;
import com.logistica.domain.repositories.PagoRepository;
import com.logistica.domain.services.AuditoriaPagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadoPagoUseCaseTest {

    @Mock
    private PagoRepository pagoRepository;

    private ConsultarEstadoPagoUseCase consultarEstadoPagoUseCase;

    private static final UUID USUARIO_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @BeforeEach
    void setUp() {
        consultarEstadoPagoUseCase = new ConsultarEstadoPagoUseCase(pagoRepository, new AuditoriaPagoService());
    }

    @Test
    void ejecutar_CuandoPagoExisteYEsDelUsuario_RetornaEstadoPagoDTO() {
        UUID pagoId = UUID.randomUUID();
        Pago pago = new Pago(
                pagoId,
                USUARIO_ID,
                new BigDecimal("1000.00"),
                LocalDateTime.now(),
                null,
                new BigDecimal("1000.00"),
                UUID.randomUUID(),
                EstadoPagoEnum.PAGADO
        );
        when(pagoRepository.findById(pagoId)).thenReturn(Optional.of(pago));

        EstadoPagoResponseDTO result = consultarEstadoPagoUseCase.ejecutar(pagoId, USUARIO_ID);

        assertNotNull(result);
        assertEquals(pagoId, result.getPagoId());
        assertEquals(EstadoPagoEnum.PAGADO.name(), result.getEstado());
        assertEquals(pago.getMontoNeto(), result.getMonto());
    }

    @Test
    void ejecutar_CuandoPagoNoExiste_LanzaPagoNoEncontradoException() {
        UUID pagoId = UUID.randomUUID();
        when(pagoRepository.findById(pagoId)).thenReturn(Optional.empty());

        assertThrows(PagoNoEncontradoException.class,
                () -> consultarEstadoPagoUseCase.ejecutar(pagoId, USUARIO_ID));
    }

    @Test
    void ejecutar_CuandoPagoNoEsDelUsuario_LanzaAccessDeniedPaymentException() {
        UUID pagoId = UUID.randomUUID();
        UUID otroPropietario = UUID.fromString("22222222-2222-2222-2222-222222222222");
        Pago pago = new Pago(
                pagoId,
                otroPropietario,
                new BigDecimal("1000.00"),
                LocalDateTime.now(),
                null,
                new BigDecimal("1000.00"),
                UUID.randomUUID(),
                EstadoPagoEnum.PAGADO
        );
        when(pagoRepository.findById(pagoId)).thenReturn(Optional.of(pago));

        assertThrows(AccessDeniedPaymentException.class,
                () -> consultarEstadoPagoUseCase.ejecutar(pagoId, USUARIO_ID));
    }
}
