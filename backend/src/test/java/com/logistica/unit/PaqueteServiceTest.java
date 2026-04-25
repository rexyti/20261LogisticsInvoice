package com.logistica.unit;

import com.logistica.application.dtos.response.SincronizacionResultadoDTO;
import com.logistica.application.usecases.paquete.PaqueteService;
import com.logistica.domain.enums.EstadoPaquete;
import com.logistica.domain.models.Paquete;
import com.logistica.domain.repositories.HistorialRepository;
import com.logistica.domain.repositories.LogSincronizacionRepository;
import com.logistica.domain.repositories.PaqueteRepository;
import com.logistica.infrastructure.http.clients.PackageApiClient;
import com.logistica.infrastructure.http.dto.GestionPaqueteDTO;
import com.logistica.infrastructure.http.mappers.GestionPaqueteMapper;
import com.logistica.shared.exceptions.PaqueteNoEncontradoException;
import com.logistica.shared.exceptions.PendienteSincronizacionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class PaqueteServiceTest {

    @Mock private PackageApiClient packageApiClient;
    @Mock private PaqueteRepository paqueteRepository;
    @Mock private HistorialRepository historialRepository;
    @Mock private LogSincronizacionRepository logRepository;
    @Mock private GestionPaqueteMapper gestionPaqueteMapper;

    @InjectMocks
    private PaqueteService service;

    private UUID idRuta;
    private UUID idPaquete;

    @BeforeEach
    void setUp() {
        idRuta    = UUID.randomUUID();
        idPaquete = UUID.randomUUID();

        when(logRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(paqueteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(historialRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(paqueteRepository.findById(idPaquete)).thenReturn(Optional.empty());
    }

    // T009: estadoActual guardado en Paquete coincide con el estado recibido (SC-001)
    @Test
    void sincronizarEstado_exitoso_guardaEstadoActualConsistente() throws Exception {
        GestionPaqueteDTO dto = new GestionPaqueteDTO(idPaquete.toString(), "ENTREGADO");
        when(packageApiClient.consultarEstado(idRuta, idPaquete))
                .thenReturn(CompletableFuture.completedFuture(dto));
        when(gestionPaqueteMapper.mapearEstado(dto)).thenReturn(Optional.of(EstadoPaquete.ENTREGADO));

        SincronizacionResultadoDTO resultado = service.sincronizarEstado(idRuta, idPaquete);

        assertEquals("ENTREGADO", resultado.estado());
        assertEquals(100, resultado.porcentajePago());

        ArgumentCaptor<Paquete> paqueteCaptor = ArgumentCaptor.forClass(Paquete.class);
        verify(paqueteRepository).save(paqueteCaptor.capture());
        assertEquals(EstadoPaquete.ENTREGADO, paqueteCaptor.getValue().estadoActual());
        verify(historialRepository).save(any());
        verify(logRepository).save(any());
    }

    // T010: dos sincronizaciones sucesivas agregan entrada en historial sin sobrescribir la anterior
    @Test
    void sincronizarEstado_dosVeces_agregaEntradaEnHistorialSinSobrescribir() throws Exception {
        GestionPaqueteDTO dto1 = new GestionPaqueteDTO(idPaquete.toString(), "DEVUELTO");
        GestionPaqueteDTO dto2 = new GestionPaqueteDTO(idPaquete.toString(), "ENTREGADO");

        when(packageApiClient.consultarEstado(idRuta, idPaquete))
                .thenReturn(CompletableFuture.completedFuture(dto1))
                .thenReturn(CompletableFuture.completedFuture(dto2));
        when(gestionPaqueteMapper.mapearEstado(dto1)).thenReturn(Optional.of(EstadoPaquete.DEVUELTO));
        when(gestionPaqueteMapper.mapearEstado(dto2)).thenReturn(Optional.of(EstadoPaquete.ENTREGADO));
        when(paqueteRepository.findById(idPaquete))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new Paquete(idPaquete, idRuta, EstadoPaquete.DEVUELTO)));

        service.sincronizarEstado(idRuta, idPaquete);
        service.sincronizarEstado(idRuta, idPaquete);

        // Dos inserciones en historial (no sobrescritura)
        verify(historialRepository, times(2)).save(any());
        // La segunda sincronización actualiza estadoActual a ENTREGADO
        ArgumentCaptor<Paquete> captor = ArgumentCaptor.forClass(Paquete.class);
        verify(paqueteRepository, times(2)).save(captor.capture());
        assertEquals(EstadoPaquete.ENTREGADO, captor.getAllValues().get(1).estadoActual());
    }

    // T011: HTTP 404 → registra error en LogSincronizacion, detiene cálculo
    @Test
    void sincronizarEstado_paqueteNoEncontrado_registraErrorYNoInsertaHistorial() throws Exception {
        when(packageApiClient.consultarEstado(idRuta, idPaquete))
                .thenReturn(CompletableFuture.failedFuture(new PaqueteNoEncontradoException(idPaquete)));

        SincronizacionResultadoDTO resultado = service.sincronizarEstado(idRuta, idPaquete);

        assertEquals("PAQUETE_NO_ENCONTRADO", resultado.estado());
        verify(logRepository).save(any());
        verify(historialRepository, never()).save(any());
    }

    // T012: timeout / reintentos agotados → marca PENDIENTE_SINCRONIZACION
    @Test
    void sincronizarEstado_timeout_marcaPendienteSincronizacion() throws Exception {
        when(packageApiClient.consultarEstado(idRuta, idPaquete))
                .thenReturn(CompletableFuture.failedFuture(
                        new PendienteSincronizacionException(idPaquete, new RuntimeException("timeout"))));

        SincronizacionResultadoDTO resultado = service.sincronizarEstado(idRuta, idPaquete);

        assertEquals("PENDIENTE_SINCRONIZACION", resultado.estado());
        verify(logRepository).save(any());
        ArgumentCaptor<Paquete> captor = ArgumentCaptor.forClass(Paquete.class);
        verify(paqueteRepository).save(captor.capture());
        assertEquals(EstadoPaquete.PENDIENTE_SINCRONIZACION, captor.getValue().estadoActual());
    }

    // T013: estado no mapeado → omite cálculo de pago, registra en LogSincronizacion
    @Test
    void sincronizarEstado_estadoNoMapeado_omiteCalculoYRegistraLog() throws Exception {
        GestionPaqueteDTO dto = new GestionPaqueteDTO(idPaquete.toString(), "EN_INSPECCION");
        when(packageApiClient.consultarEstado(idRuta, idPaquete))
                .thenReturn(CompletableFuture.completedFuture(dto));
        when(gestionPaqueteMapper.mapearEstado(dto)).thenReturn(Optional.empty());

        SincronizacionResultadoDTO resultado = service.sincronizarEstado(idRuta, idPaquete);

        assertEquals("ESTADO_NO_MAPEADO", resultado.estado());
        assertNull(resultado.porcentajePago());
        verify(logRepository).save(any());
        verify(historialRepository, never()).save(any());
    }
}
