package com.logistica.application.usecases;

import com.logistica.application.dtos.request.*;
import com.logistica.application.mappers.RutaEventMapper;
import com.logistica.application.usecases.ruta.ProcesarRutaCerradaUseCase;
import com.logistica.domain.enums.EstadoParada;
import com.logistica.domain.enums.EstadoProcesamiento;
import com.logistica.domain.enums.TipoAlertaRuta;
import com.logistica.domain.events.RutaCerradaProcesadaEvent;
import com.logistica.domain.exceptions.RutaInvalidaException;
import com.logistica.domain.models.Parada;
import com.logistica.domain.models.Ruta;
import com.logistica.domain.repositories.RutaRepository;
import com.logistica.domain.repositories.TarifaRepository;
import com.logistica.domain.services.ClasificacionRutaService;
import com.logistica.domain.validators.RutaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcesarRutaCerradaUseCase - Tests")
class ProcesarRutaCerradaUseCaseTest {

    @Mock
    private RutaRepository rutaRepository;
    @Mock
    private TarifaRepository tarifaRepository;
    @Mock
    private RutaEventMapper rutaEventMapper;
    @Mock
    private RutaValidator rutaValidator;
    @Mock
    private ClasificacionRutaService clasificacionRutaService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private ProcesarRutaCerradaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ProcesarRutaCerradaUseCase(
                rutaRepository,
                tarifaRepository,
                rutaEventMapper,
                rutaValidator,
                clasificacionRutaService,
                eventPublisher
        );
    }

    // ── T012: Idempotencia ─────────────────────────────────────────────────────
    @Test
    @DisplayName("Debe ignorar evento duplicado si la ruta ya existe")
    void debe_ignorar_evento_duplicado() {
        UUID rutaId = UUID.randomUUID();
        RutaCerradaEventDTO dto = buildEvento(rutaId, "Recorrido completo", "MOTO", List.of());

        when(rutaRepository.existsByRutaId(rutaId)).thenReturn(true);

        useCase.ejecutar(dto);

        verify(rutaRepository, never()).guardar(any());
        verifyNoInteractions(rutaEventMapper, rutaValidator, clasificacionRutaService, eventPublisher);
    }

    // ── T013: Total de paradas coincide ───────────────────────────────────────
    @Test
    @DisplayName("Debe persistir la ruta con el mismo número de paradas que el evento")
    void debe_persistir_mismo_numero_de_paradas_que_el_evento() {
        UUID rutaId = UUID.randomUUID();
        List<com.logistica.domain.models.Parada> domainParadas = List.of(
                mock(com.logistica.domain.models.Parada.class),
                mock(com.logistica.domain.models.Parada.class)
        );
        RutaCerradaEventDTO dto = buildEvento(rutaId, "Recorrido completo", "MOTO", List.of(parada("EXITOSA", null), parada("FALLIDA", "CLIENTE_AUSENTE")));
        Ruta rutaMock = buildRutaMock(rutaId, "Recorrido completo", "MOTO", domainParadas);

        when(rutaRepository.existsByRutaId(rutaId)).thenReturn(false);
        when(rutaEventMapper.toDomain(any(RutaCerradaEventDTO.class))).thenReturn(rutaMock);
        when(rutaRepository.guardar(any(Ruta.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.ejecutar(dto);

        verify(rutaRepository).guardar(argThat(ruta -> ruta.getParadas().size() == 2));
        verify(rutaValidator).validar(eq(rutaMock), eq(2));
        verify(clasificacionRutaService).clasificar(eq(rutaMock));
    }

    // ── T014: Contrato nulo ───────────────────────────────────────────────────
    @Test
    @DisplayName("Debe marcar revisión y publicar evento por contrato nulo")
    void debe_marcar_revision_y_publicar_evento_por_contrato_nulo() {
        UUID rutaId = UUID.randomUUID();
        RutaCerradaEventDTO dto = buildEvento(rutaId, null, "MOTO", List.of(parada("EXITOSA", null)));
        Ruta rutaMock = buildRutaMock(rutaId, null, "MOTO", List.of(mock(com.logistica.domain.models.Parada.class)));

        when(rutaRepository.existsByRutaId(rutaId)).thenReturn(false);
        when(rutaEventMapper.toDomain(any(RutaCerradaEventDTO.class))).thenReturn(rutaMock);
        when(rutaRepository.guardar(any(Ruta.class))).thenAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<RutaCerradaProcesadaEvent> eventCaptor = ArgumentCaptor.forClass(RutaCerradaProcesadaEvent.class);

        useCase.ejecutar(dto);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        RutaCerradaProcesadaEvent event = eventCaptor.getValue();

        assertThat(event.getEstadoProcesamiento()).isEqualTo(EstadoProcesamiento.REQUIERE_REVISION);
        assertThat(event.getTipoAlerta()).isEqualTo(TipoAlertaRuta.CONTRATO_NULO);
        assertThat(rutaMock.getEstadoProcesamiento()).isEqualTo(EstadoProcesamiento.REQUIERE_REVISION); // Verify state change on mock

        verify(rutaRepository).guardar(eq(rutaMock));
        verify(rutaValidator).validar(eq(rutaMock), eq(1));
        verify(clasificacionRutaService).clasificar(eq(rutaMock));
    }

    // ── T015: Vehículo desconocido ────────────────────────────────────────────
    @Test
    @DisplayName("Debe marcar revisión y publicar evento por vehículo desconocido")
    void debe_marcar_revision_y_publicar_evento_por_vehiculo_desconocido() {
        UUID rutaId = UUID.randomUUID();
        RutaCerradaEventDTO dto = buildEvento(rutaId, "Recorrido completo", "BICICLETA", List.of(parada("EXITOSA", null)));
        Ruta rutaMock = buildRutaMock(rutaId, "Recorrido completo", "BICICLETA", List.of(mock(com.logistica.domain.models.Parada.class)));

        when(rutaRepository.existsByRutaId(rutaId)).thenReturn(false);
        when(rutaEventMapper.toDomain(any(RutaCerradaEventDTO.class))).thenReturn(rutaMock);
        when(rutaRepository.guardar(any(Ruta.class))).thenAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<RutaCerradaProcesadaEvent> eventCaptor = ArgumentCaptor.forClass(RutaCerradaProcesadaEvent.class);

        useCase.ejecutar(dto);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        RutaCerradaProcesadaEvent event = eventCaptor.getValue();

        assertThat(event.getEstadoProcesamiento()).isEqualTo(EstadoProcesamiento.REQUIERE_REVISION);
        assertThat(event.getTipoAlerta()).isEqualTo(TipoAlertaRuta.VEHICULO_DESCONOCIDO);
        assertThat(rutaMock.getEstadoProcesamiento()).isEqualTo(EstadoProcesamiento.REQUIERE_REVISION); // Verify state change on mock

        verify(rutaRepository).guardar(eq(rutaMock));
        verify(rutaValidator).validar(eq(rutaMock), eq(1));
        verify(clasificacionRutaService).clasificar(eq(rutaMock));
    }

    // ── T016: DTO nulo ────────────────────────────────────────────────────────
    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si el DTO de entrada es nulo")
    void debe_lanzar_excepcion_si_dto_es_null() {
        assertThatThrownBy(() -> useCase.ejecutar(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Evento no puede ser null"); // Assuming RutaEventMapper throws this
    }

    // ── T017: Ruta sin paquetes ───────────────────────────────────────────────
    @Test
    @DisplayName("Debe lanzar RutaInvalidaException si una parada no tiene paqueteId")
    void debe_lanzar_excepcion_si_parada_no_tiene_paqueteId() {
        UUID rutaId = UUID.randomUUID();


        ParadaEventDTO paradaDTO = new ParadaEventDTO();
        paradaDTO.setParadaId(UUID.randomUUID());
        paradaDTO.setEstado(EstadoParada.EXITOSA);


        Parada paradaSinPaquete = Parada.builder()
                .paradaId(paradaDTO.getParadaId())
                .paqueteId(null)
                .estado(EstadoParada.EXITOSA)
                .build();

        Ruta rutaMock = Ruta.builder()
                .rutaId(rutaId)
                .modeloContrato("Recorrido completo")
                .tipoVehiculo("MOTO")
                .estadoProcesamiento(EstadoProcesamiento.OK)
                .paradas(List.of(paradaSinPaquete))
                .build();

        RutaCerradaEventDTO dto = buildEvento(
                rutaId, "Recorrido completo", "MOTO", List.of(paradaDTO)); // ← lista, no int

        when(rutaRepository.existsByRutaId(rutaId)).thenReturn(false);
        when(rutaEventMapper.toDomain(any())).thenReturn(rutaMock);

        ProcesarRutaCerradaUseCase useCaseConValidadorReal = new ProcesarRutaCerradaUseCase(
                rutaRepository,
                tarifaRepository,
                rutaEventMapper,
                new RutaValidator(),   // ← real
                clasificacionRutaService,
                eventPublisher
        );

        assertThatThrownBy(() -> useCaseConValidadorReal.ejecutar(dto))
                .isInstanceOf(RutaInvalidaException.class)
                .hasMessageContaining("Parada sin paqueteId");
    }


    // ── Helpers ───────────────────────────────────────────────────────────────

    private RutaCerradaEventDTO buildEvento(
            UUID rutaId, String contrato, String vehiculoTipo, List<ParadaEventDTO> paradas) {

        ConductorEventDTO conductor = new ConductorEventDTO();
        conductor.setConductorId(UUID.randomUUID());
        conductor.setModeloContrato(contrato);

        VehiculoEventDTO vehiculo = new VehiculoEventDTO();
        vehiculo.setVehiculoId(UUID.randomUUID());
        vehiculo.setTipo(vehiculoTipo);

        RutaCerradaEventDTO evento = new RutaCerradaEventDTO();
        evento.setRutaId(rutaId);
        evento.setConductor(conductor);
        evento.setVehiculo(vehiculo);
        evento.setFechaHoraCierre(LocalDateTime.now());
        evento.setParadas(paradas);

        return evento;
    }

    private Ruta buildRutaMock(UUID rutaId, String modeloContrato, String tipoVehiculo, List<com.logistica.domain.models.Parada> paradas) {
        return Ruta.builder()
                .rutaId(rutaId)
                .modeloContrato(modeloContrato)
                .tipoVehiculo(tipoVehiculo)
                .paradas(paradas)
                .build();
    }

    private ParadaEventDTO parada(String estado, String motivo) {
        ParadaEventDTO p = new ParadaEventDTO();
        p.setParadaId(UUID.randomUUID());
        p.setEstado(EstadoParada.valueOf(estado));
        p.setMotivoNoEntrega(motivo);
        return p;
    }
}
