package com.logistica.application.usecases;

import com.logistica.cierreRuta.application.dtos.request.CierreRutaConductorEventDTO;
import com.logistica.cierreRuta.application.dtos.request.CierreRutaParadaEventDTO;
import com.logistica.cierreRuta.application.dtos.request.CierreRutaRutaCerradaEventDTO;
import com.logistica.cierreRuta.application.dtos.request.CierreRutaVehiculoEventDTO;
import com.logistica.cierreRuta.application.mappers.CierreRutaRutaEventMapper;
import com.logistica.cierreRuta.application.usecases.ruta.CierreRutaProcesarRutaCerradaUseCase;
import com.logistica.cierreRuta.domain.enums.EstadoParada;
import com.logistica.cierreRuta.domain.enums.EstadoProcesamiento;
import com.logistica.cierreRuta.domain.enums.TipoAlertaRuta;
import com.logistica.cierreRuta.domain.events.RutaCerradaProcesadaEvent;
import com.logistica.cierreRuta.domain.exceptions.RutaInvalidaException;
import com.logistica.cierreRuta.domain.models.Parada;
import com.logistica.cierreRuta.domain.models.CierreRutaRuta;
import com.logistica.cierreRuta.domain.models.CierreRutaTransportista;
import com.logistica.cierreRuta.domain.ports.DomainEvent;
import com.logistica.cierreRuta.domain.ports.EventPublisher;
import com.logistica.cierreRuta.domain.ports.TimeProvider;
import com.logistica.cierreRuta.domain.repositories.RutaRepository;
import com.logistica.cierreRuta.domain.repositories.CierreRutaTransportistaRepository;
import com.logistica.cierreRuta.domain.services.ClasificacionRutaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CierreRutaProcesarRutaCerradaUseCase - Tests")
class CierreRutaProcesarRutaCerradaUseCaseTest {

    @Mock private RutaRepository rutaRepository;
    @Mock private CierreRutaTransportistaRepository transportistaRepository;
    @Mock private CierreRutaRutaEventMapper rutaEventMapper;
    @Mock private TimeProvider timeProvider;
    @Mock private ClasificacionRutaService clasificacionRutaService;
    @Mock private EventPublisher eventPublisher;

    private CierreRutaProcesarRutaCerradaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CierreRutaProcesarRutaCerradaUseCase(
                rutaRepository,
                transportistaRepository,
                rutaEventMapper,
                timeProvider,
                clasificacionRutaService,
                eventPublisher
        );
        when(timeProvider.now()).thenReturn(LocalDateTime.now());
    }

    // ── T012: Idempotencia ─────────────────────────────────────────────────────
    @Test
    @DisplayName("Debe ignorar evento duplicado si la ruta ya existe")
    void debe_ignorar_evento_duplicado() {
        UUID rutaId = UUID.randomUUID();
        CierreRutaRutaCerradaEventDTO dto = buildEvento(rutaId, "Recorrido completo", "MOTO", List.of());

        when(rutaRepository.existsByRutaId(rutaId)).thenReturn(true);

        useCase.ejecutar(dto);

        verify(rutaRepository, never()).guardar(any());
        verifyNoInteractions(rutaEventMapper, transportistaRepository, clasificacionRutaService, eventPublisher);
    }

    // ── T013: Clasificar, procesar y persistir ────────────────────────────────
    @Test
    @DisplayName("Debe buscar el transportista, clasificar, procesar y persistir la ruta")
    void debe_clasificar_procesar_y_persistir_la_ruta() {
        UUID rutaId = UUID.randomUUID();
        CierreRutaRutaCerradaEventDTO dto = buildEvento(rutaId, "Recorrido completo", "MOTO",
                List.of(parada("EXITOSA", null), parada("EXITOSA", null)));

        CierreRutaTransportista transportista = transportista();
        CierreRutaRuta rutaMock = mock(CierreRutaRuta.class);
        when(rutaMock.getTransportista()).thenReturn(transportista);
        when(rutaMock.obtenerEventos()).thenReturn(List.of());

        when(rutaRepository.existsByRutaId(rutaId)).thenReturn(false);
        when(rutaEventMapper.toDomain(any())).thenReturn(rutaMock);
        when(transportistaRepository.buscarPorTransportistaId(transportista.getTransportistaId()))
                .thenReturn(Optional.of(transportista));

        useCase.ejecutar(dto);

        verify(transportistaRepository).buscarPorTransportistaId(transportista.getTransportistaId());
        verify(rutaMock).asignarTransportista(transportista);
        verify(clasificacionRutaService).clasificar(rutaMock);
        verify(rutaMock).procesar(any(LocalDateTime.class));
        verify(rutaRepository).guardar(rutaMock);
        verifyNoInteractions(eventPublisher);
    }

    // ── T013b: CierreRutaTransportista nuevo se guarda ──────────────────────────────────
    @Test
    @DisplayName("Debe guardar el transportista si no existe en el sistema")
    void debe_guardar_transportista_si_no_existe() {
        UUID rutaId = UUID.randomUUID();
        CierreRutaRutaCerradaEventDTO dto = buildEvento(rutaId, "Recorrido completo", "MOTO", List.of());

        CierreRutaTransportista transportista = transportista();
        CierreRutaRuta rutaMock = mock(CierreRutaRuta.class);
        when(rutaMock.getTransportista()).thenReturn(transportista);
        when(rutaMock.obtenerEventos()).thenReturn(List.of());

        when(rutaRepository.existsByRutaId(rutaId)).thenReturn(false);
        when(rutaEventMapper.toDomain(any())).thenReturn(rutaMock);
        when(transportistaRepository.buscarPorTransportistaId(transportista.getTransportistaId()))
                .thenReturn(Optional.empty());
        when(transportistaRepository.guardar(transportista)).thenReturn(transportista);

        useCase.ejecutar(dto);

        verify(transportistaRepository).guardar(transportista);
        verify(rutaMock).asignarTransportista(transportista);
    }

    // ── T014: Contrato nulo → CONTRATO_NULO ──────────────────────────────────
    @Test
    @DisplayName("Debe publicar evento CONTRATO_NULO cuando el modelo de contrato es nulo")
    void debe_publicar_evento_contrato_nulo() {
        UUID rutaId = UUID.randomUUID();
        CierreRutaRutaCerradaEventDTO dto = buildEvento(rutaId, null, "MOTO", List.of(parada("EXITOSA", null)));

        DomainEvent eventoEsperado = new RutaCerradaProcesadaEvent(
                rutaId, EstadoProcesamiento.REQUIERE_REVISION,
                TipoAlertaRuta.CONTRATO_NULO, "Contrato no encontrado", LocalDateTime.now());

        CierreRutaTransportista transportista = transportista();
        CierreRutaRuta rutaMock = mock(CierreRutaRuta.class);
        when(rutaMock.getTransportista()).thenReturn(transportista);
        when(rutaMock.obtenerEventos()).thenReturn(List.of(eventoEsperado));

        when(rutaRepository.existsByRutaId(rutaId)).thenReturn(false);
        when(rutaEventMapper.toDomain(any())).thenReturn(rutaMock);
        when(transportistaRepository.buscarPorTransportistaId(transportista.getTransportistaId()))
                .thenReturn(Optional.of(transportista));

        useCase.ejecutar(dto);

        verify(eventPublisher).publish(eventoEsperado);
        verify(rutaRepository).guardar(rutaMock);
    }

    // ── T015: Vehículo nulo → VEHICULO_DESCONOCIDO ───────────────────────────
    @Test
    @DisplayName("Debe publicar evento VEHICULO_DESCONOCIDO cuando el tipo de vehículo es nulo")
    void debe_publicar_evento_vehiculo_desconocido() {
        UUID rutaId = UUID.randomUUID();
        CierreRutaRutaCerradaEventDTO dto = buildEvento(rutaId, "Recorrido completo", null,
                List.of(parada("EXITOSA", null)));

        DomainEvent eventoEsperado = new RutaCerradaProcesadaEvent(
                rutaId, EstadoProcesamiento.REQUIERE_REVISION,
                TipoAlertaRuta.VEHICULO_DESCONOCIDO, "Vehículo desconocido", LocalDateTime.now());

        CierreRutaTransportista transportista = transportista();
        CierreRutaRuta rutaMock = mock(CierreRutaRuta.class);
        when(rutaMock.getTransportista()).thenReturn(transportista);
        when(rutaMock.obtenerEventos()).thenReturn(List.of(eventoEsperado));

        when(rutaRepository.existsByRutaId(rutaId)).thenReturn(false);
        when(rutaEventMapper.toDomain(any())).thenReturn(rutaMock);
        when(transportistaRepository.buscarPorTransportistaId(transportista.getTransportistaId()))
                .thenReturn(Optional.of(transportista));

        useCase.ejecutar(dto);

        verify(eventPublisher).publish(eventoEsperado);
        verify(rutaRepository).guardar(rutaMock);
    }

    // ── T016: DTO nulo ────────────────────────────────────────────────────────
    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si el DTO de entrada es nulo")
    void debe_lanzar_excepcion_si_dto_es_null() {
        assertThatThrownBy(() -> useCase.ejecutar(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Evento no puede ser null");
    }

    // ── T017: Parada sin paqueteId ────────────────────────────────────────────
    @Test
    @DisplayName("Debe lanzar RutaInvalidaException si una parada no tiene paqueteId")
    void debe_lanzar_excepcion_si_parada_no_tiene_paqueteId() {
        UUID rutaId = UUID.randomUUID();
        CierreRutaTransportista transportista = transportista();

        Parada paradaSinPaquete = Parada.builder()
                .paradaId(UUID.randomUUID())
                .paqueteId(null)
                .estado(EstadoParada.EXITOSA)
                .build();

        CierreRutaRuta rutaReal = CierreRutaRuta.builder()
                .rutaId(rutaId)
                .modeloContrato("Recorrido completo")
                .transportista(transportista)
                .parada(paradaSinPaquete)
                .build();

        CierreRutaRutaCerradaEventDTO dto = buildEvento(rutaId, "Recorrido completo", "MOTO", List.of());

        when(rutaRepository.existsByRutaId(rutaId)).thenReturn(false);
        when(rutaEventMapper.toDomain(any())).thenReturn(rutaReal);
        when(transportistaRepository.buscarPorTransportistaId(transportista.getTransportistaId()))
                .thenReturn(Optional.of(transportista));

        assertThatThrownBy(() -> useCase.ejecutar(dto))
                .isInstanceOf(RutaInvalidaException.class)
                .hasMessageContaining("Parada sin paqueteId");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CierreRutaTransportista transportista() {
        return CierreRutaTransportista.builder()
                .transportistaId(UUID.randomUUID())
                .nombre("Conductor Test")
                .build();
    }

    private CierreRutaRutaCerradaEventDTO buildEvento(
            UUID rutaId, String contrato, String vehiculoTipo, List<CierreRutaParadaEventDTO> paradas) {

        CierreRutaConductorEventDTO conductor = new CierreRutaConductorEventDTO();
        conductor.setConductorId(UUID.randomUUID());
        conductor.setModeloContrato(contrato);

        CierreRutaVehiculoEventDTO vehiculo = new CierreRutaVehiculoEventDTO();
        vehiculo.setVehiculoId(UUID.randomUUID());
        vehiculo.setTipo(vehiculoTipo);

        CierreRutaRutaCerradaEventDTO evento = new CierreRutaRutaCerradaEventDTO();
        evento.setRutaId(rutaId);
        evento.setConductor(conductor);
        evento.setVehiculo(vehiculo);
        evento.setFechaHoraCierre(LocalDateTime.now());
        evento.setParadas(paradas);

        return evento;
    }

    private CierreRutaParadaEventDTO parada(String estado, String motivo) {
        CierreRutaParadaEventDTO p = new CierreRutaParadaEventDTO();
        p.setParadaId(UUID.randomUUID());
        p.setEstado(EstadoParada.valueOf(estado));
        p.setMotivoNoEntrega(motivo);
        return p;
    }
}
