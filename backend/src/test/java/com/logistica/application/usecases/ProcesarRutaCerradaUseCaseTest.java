package com.logistica.application.usecases;

import com.logistica.application.dtos.request.*;
import com.logistica.application.mappers.RutaEventMapper;
import com.logistica.application.usecases.ruta.ProcesarRutaCerradaUseCase;
import com.logistica.domain.enums.EstadoParada;
import com.logistica.domain.enums.EstadoProcesamiento;
import com.logistica.domain.models.Ruta;
import com.logistica.domain.repositories.RutaRepository;
import com.logistica.domain.services.ClasificacionRutaService;
import com.logistica.domain.validators.RutaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcesarRutaCerradaUseCase - Tests de Integración")
class ProcesarRutaCerradaUseCaseTest {

    @Mock
    private RutaRepository rutaRepository;

    @Mock
    private RutaEventMapper rutaEventMapper;

    @Mock
    private RutaValidator rutaValidator;

    @Mock
    private ClasificacionRutaService clasificacionRutaService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private ProcesarRutaCerradaUseCase useCase;

    private RutaCerradaEventDTO dto;
    private Ruta rutaMock;

    @BeforeEach
    void setUp() {
        useCase = new ProcesarRutaCerradaUseCase(
                rutaRepository,
                rutaEventMapper,
                rutaValidator,
                clasificacionRutaService,
                eventPublisher
        );

        UUID rutaId = UUID.randomUUID();
        dto = buildEvento(rutaId, "Recorrido completo", "MOTO");
        
        rutaMock = Ruta.builder()
                .rutaId(rutaId)
                .modeloContrato("Recorrido completo")
                .tipoVehiculo("MOTO")
                .paradas(List.of())
                .build();
    }

    @Test
    @DisplayName("Debe procesar el evento correctamente y persistir la ruta")
    void procesa_evento_correctamente() {
        // Given
        when(rutaRepository.existsByRutaId(any())).thenReturn(false);
        when(rutaEventMapper.toDomain(any())).thenReturn(rutaMock);

        // When
        useCase.ejecutar(dto);

        // Then
        verify(rutaRepository, times(1)).guardar(any(Ruta.class));
        verify(clasificacionRutaService, times(1)).clasificar(any(Ruta.class));
    }

    @Test
    void lanza_excepcion_si_dto_es_null() {

        assertThatThrownBy(() -> useCase.ejecutar(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Evento de ruta no puede ser null");
    }

    @Test
    @DisplayName("Debe ignorar eventos duplicados (Idempotencia)")
    void debe_ignorar_evento_duplicado() {
        // Given
        when(rutaRepository.existsByRutaId(any())).thenReturn(true);

        // When
        useCase.ejecutar(dto);

        // Then
        verify(rutaRepository, never()).guardar(any());
        verifyNoInteractions(eventPublisher);
    }


    // --- Helpers de Construcción ---

    private RutaCerradaEventDTO buildEvento(UUID rutaId, String contrato, String vehiculoTipo) {
        ConductorEventDTO conductor = new ConductorEventDTO();
        conductor.setConductorId(UUID.randomUUID());
        conductor.setModeloContrato(contrato);

        VehiculoEventDTO vehiculo = new VehiculoEventDTO();
        vehiculo.setTipo(vehiculoTipo);

        RutaCerradaEventDTO evento = new RutaCerradaEventDTO();
        evento.setRutaId(rutaId);
        evento.setConductor(conductor);
        evento.setVehiculo(vehiculo);
        evento.setFechaHoraCierre(LocalDateTime.now());
        evento.setParadas(List.of());

        return evento;
    }
}
