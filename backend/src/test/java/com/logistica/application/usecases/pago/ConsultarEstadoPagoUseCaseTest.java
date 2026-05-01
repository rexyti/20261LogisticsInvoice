package com.logistica.application.usecases.pago;

import com.logistica.application.dtos.response.EstadoPagoResponseDTO;
import com.logistica.domain.enums.EstadoPagoEnum;
import com.logistica.domain.exceptions.PagoNoEncontradoException;
import com.logistica.domain.models.Pago;
import com.logistica.domain.repositories.PagoRepository;
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
public class ConsultarEstadoPagoUseCaseTest {

    @Mock
    private PagoRepository pagoRepository;

    private ConsultarEstadoPagoUseCase consultarEstadoPagoUseCase;

    @BeforeEach
    void setUp() {
        consultarEstadoPagoUseCase = new ConsultarEstadoPagoUseCase(pagoRepository);
    }

    @Test
    void ejecutar_CuandoPagoExiste_DebeRetornarEstadoPagoDTO() {
        // Arrange
        UUID pagoId = UUID.randomUUID();
        Pago pago = new Pago(
                pagoId,
                UUID.randomUUID(),
                new BigDecimal("1000.00"),
                LocalDateTime.now(),
                null,
                new BigDecimal("1000.00"),
                UUID.randomUUID(),
                EstadoPagoEnum.PAGADO
        );
        when(pagoRepository.findById(pagoId)).thenReturn(Optional.of(pago));

        // Act
        EstadoPagoResponseDTO result = consultarEstadoPagoUseCase.ejecutar(pagoId);

        // Assert
        assertNotNull(result);
        assertEquals(pagoId, result.getPagoId());
        assertEquals(EstadoPagoEnum.PAGADO.name(), result.getEstado());
        assertEquals(pago.getMontoNeto(), result.getMonto());
    }

    @Test
    void ejecutar_CuandoPagoNoExiste_DebeLanzarPagoNoEncontradoException() {
        // Arrange
        UUID pagoId = UUID.randomUUID();
        when(pagoRepository.findById(pagoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PagoNoEncontradoException.class, () -> {
            consultarEstadoPagoUseCase.ejecutar(pagoId);
        });
    }
}
