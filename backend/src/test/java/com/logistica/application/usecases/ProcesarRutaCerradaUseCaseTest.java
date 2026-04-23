package com.logistica.application.usecases;

import com.logistica.application.dtos.request.ConductorEventDTO;
import com.logistica.application.dtos.request.ParadaEventDTO;
import com.logistica.application.dtos.request.RutaCerradaEventDTO;
import com.logistica.application.dtos.request.VehiculoEventDTO;
import com.logistica.application.usecases.ruta.ProcesarRutaCerradaUseCase;
import com.logistica.domain.events.RutaCerradaProcesadaEvent;
import com.logistica.domain.models.Ruta;
import com.logistica.domain.repositories.RutaRepository;
import com.logistica.domain.validators.RutaValidator;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * T012–T015 — Idempotencia, conteo de paradas, contrato nulo y vehículo desconocido.
 */
@ExtendWith(MockitoExtension.class)
class ProcesarRutaCerradaUseCaseTest {

    @Mock
    private RutaRepository rutaRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private ProcesarRutaCerradaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ProcesarRutaCerradaUseCase(rutaRepository, new RutaValidator(), eventPublisher);
    }

    // ── T012: Idempotencia ─────────────────────────────────────────────────────

    @Test
    void ignoraEventoDuplicado() {
        UUID rutaId = UUID.randomUUID();
        when(rutaRepository.existsByRutaId(rutaId)).thenReturn(true);

        useCase.ejecutar(eventoValido(rutaId));

        verify(rutaRepository, never()).guardar(any());
    }

    // ── T013: Total de paradas coincide ───────────────────────────────────────

    @Test
    void persisteTotalDeParadasIgualAlEvento() {
        UUID rutaId = UUID.randomUUID();
        when(rutaRepository.existsByRutaId(rutaId)).thenReturn(false);

        ArgumentCaptor<Ruta> captor = ArgumentCaptor.forClass(Ruta.class);
        when(rutaRepository.guardar(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        RutaCerradaEventDTO evento = eventoValido(rutaId);
        useCase.ejecutar(evento);

        assertThat(captor.getValue().getParadas()).hasSize(evento.getParadas().size());
    }

    // ── T014: Contrato nulo ───────────────────────────────────────────────────

    @Test
    void contratoNuloPublicaEventoYMarcaRevision() {
        UUID rutaId = UUID.randomUUID();
        when(rutaRepository.existsByRutaId(rutaId)).thenReturn(false);
        when(rutaRepository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        RutaCerradaEventDTO evento = eventoConContratoNulo(rutaId);
        useCase.ejecutar(evento);

        ArgumentCaptor<RutaCerradaProcesadaEvent> eventCaptor =
                ArgumentCaptor.forClass(RutaCerradaProcesadaEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getTipoAlerta()).isEqualTo("CONTRATO_NULO");

        ArgumentCaptor<Ruta> rutaCaptor = ArgumentCaptor.forClass(Ruta.class);
        verify(rutaRepository).guardar(rutaCaptor.capture());
        assertThat(rutaCaptor.getValue().getEstadoProcesamiento().name()).isEqualTo("REQUIERE_REVISION");
    }

    // ── T015: Vehículo desconocido ────────────────────────────────────────────

    @Test
    void vehiculoDesconocidoPublicaEventoYMarcaRevision() {
        UUID rutaId = UUID.randomUUID();
        when(rutaRepository.existsByRutaId(rutaId)).thenReturn(false);
        when(rutaRepository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        RutaCerradaEventDTO evento = eventoConVehiculoDesconocido(rutaId);
        useCase.ejecutar(evento);

        ArgumentCaptor<RutaCerradaProcesadaEvent> eventCaptor =
                ArgumentCaptor.forClass(RutaCerradaProcesadaEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getTipoAlerta()).isEqualTo("VEHICULO_DESCONOCIDO");

        ArgumentCaptor<Ruta> rutaCaptor = ArgumentCaptor.forClass(Ruta.class);
        verify(rutaRepository).guardar(rutaCaptor.capture());
        assertThat(rutaCaptor.getValue().getEstadoProcesamiento().name()).isEqualTo("REQUIERE_REVISION");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private RutaCerradaEventDTO eventoValido(UUID rutaId) {
        return buildEvento(rutaId, "Recorrido completo", "MOTO",
                List.of(parada("EXITOSA", null), parada("FALLIDA", "CLIENTE_AUSENTE")));
    }

    private RutaCerradaEventDTO eventoConContratoNulo(UUID rutaId) {
        return buildEvento(rutaId, null, "MOTO",
                List.of(parada("EXITOSA", null)));
    }

    private RutaCerradaEventDTO eventoConVehiculoDesconocido(UUID rutaId) {
        return buildEvento(rutaId, "Recorrido completo", "BICICLETA",
                List.of(parada("EXITOSA", null)));
    }

    private RutaCerradaEventDTO buildEvento(UUID rutaId, String modeloContrato,
                                            String tipoVehiculo, List<ParadaEventDTO> paradas) {
        ConductorEventDTO conductor = new ConductorEventDTO();
        conductor.setConductorId(UUID.randomUUID());
        conductor.setNombre("Juan Pérez");
        conductor.setModeloContrato(modeloContrato);

        VehiculoEventDTO vehiculo = new VehiculoEventDTO();
        vehiculo.setVehiculoId(UUID.randomUUID());
        vehiculo.setTipo(tipoVehiculo);

        RutaCerradaEventDTO evento = new RutaCerradaEventDTO();
        evento.setTipoEvento("RUTA_CERRADA");
        evento.setRutaId(rutaId);
        evento.setFechaHoraInicioTransito(LocalDateTime.of(2026, 3, 6, 7, 45));
        evento.setFechaHoraCierre(LocalDateTime.of(2026, 3, 6, 18, 0));
        evento.setConductor(conductor);
        evento.setVehiculo(vehiculo);
        evento.setParadas(paradas);
        return evento;
    }

    private ParadaEventDTO parada(String estado, String motivo) {
        ParadaEventDTO p = new ParadaEventDTO();
        p.setParadaId(UUID.randomUUID());
        p.setEstado(estado);
        p.setMotivoNoEntrega(motivo);
        return p;
    }
}
