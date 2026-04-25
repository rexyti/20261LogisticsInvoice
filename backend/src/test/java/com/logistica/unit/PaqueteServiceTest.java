package com.logistica.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.application.dtos.response.SincronizacionResultadoDTO;
import com.logistica.application.ports.GestionPaquetePort;
import com.logistica.application.usecases.paquete.PaqueteService;
import com.logistica.domain.enums.EstadoPaquete;
import com.logistica.domain.models.GestionPaquete;
import com.logistica.domain.models.Paquete;
import com.logistica.domain.repositories.HistorialRepository;
import com.logistica.domain.repositories.LogSincronizacionRepository;
import com.logistica.domain.repositories.PaqueteRepository;
import com.logistica.domain.services.EstadoPaqueteService;
import com.logistica.shared.exceptions.PaqueteNoEncontradoException;
import com.logistica.shared.exceptions.PendienteSincronizacionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaqueteServiceTest {

    @Mock private GestionPaquetePort gestionPaquetePort;
    @Mock private PaqueteRepository paqueteRepository;
    @Mock private HistorialRepository historialRepository;
    @Mock private LogSincronizacionRepository logRepository;

    private PaqueteService service;

    private UUID idRuta;
    private UUID idPaquete;

    @BeforeEach
    void setUp() {
        idRuta = UUID.randomUUID();
        idPaquete = UUID.randomUUID();

        service = new PaqueteService(
                gestionPaquetePort,
                paqueteRepository,
                historialRepository,
                logRepository,
                new EstadoPaqueteService(),
                new ObjectMapper()
        );

        when(logRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(paqueteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(historialRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(paqueteRepository.findById(idPaquete)).thenReturn(Optional.empty());
    }

    @Test
    void sincronizarEstado_exitoso_guardaEstadoActualConsistente() throws Exception {
        GestionPaquete dto = new GestionPaquete(idPaquete.toString(), "ENTREGADO");
        when(gestionPaquetePort.consultarEstado(idRuta, idPaquete))
                .thenReturn(CompletableFuture.completedFuture(dto));

        SincronizacionResultadoDTO resultado = service.sincronizarEstado(idRuta, idPaquete);

        assertEquals("ENTREGADO", resultado.estado());
        assertEquals(100, resultado.porcentajePago());

        ArgumentCaptor<Paquete> paqueteCaptor = ArgumentCaptor.forClass(Paquete.class);
        verify(paqueteRepository).save(paqueteCaptor.capture());
        assertEquals(EstadoPaquete.ENTREGADO, paqueteCaptor.getValue().estadoActual());
        verify(historialRepository).save(any());
        verify(logRepository).save(any());
    }

    @Test
    void sincronizarEstado_estadoIgual_noDuplicaHistorial() throws Exception {
        GestionPaquete dto = new GestionPaquete(idPaquete.toString(), "ENTREGADO");
        when(gestionPaquetePort.consultarEstado(idRuta, idPaquete))
                .thenReturn(CompletableFuture.completedFuture(dto));
        when(paqueteRepository.findById(idPaquete))
                .thenReturn(Optional.of(new Paquete(idPaquete, idRuta, EstadoPaquete.ENTREGADO, 1L)));

        SincronizacionResultadoDTO resultado = service.sincronizarEstado(idRuta, idPaquete);

        assertEquals("ENTREGADO", resultado.estado());
        verify(paqueteRepository, never()).save(any());
        verify(historialRepository, never()).save(any());
        verify(logRepository).save(any());
    }

    @Test
    void sincronizarEstado_dosVeces_agregaEntradaEnHistorialSinSobrescribir() throws Exception {
        GestionPaquete dto1 = new GestionPaquete(idPaquete.toString(), "DEVUELTO");
        GestionPaquete dto2 = new GestionPaquete(idPaquete.toString(), "ENTREGADO");

        when(gestionPaquetePort.consultarEstado(idRuta, idPaquete))
                .thenReturn(CompletableFuture.completedFuture(dto1))
                .thenReturn(CompletableFuture.completedFuture(dto2));
        when(paqueteRepository.findById(idPaquete))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new Paquete(idPaquete, idRuta, EstadoPaquete.DEVUELTO, 1L)));

        service.sincronizarEstado(idRuta, idPaquete);
        service.sincronizarEstado(idRuta, idPaquete);

        verify(historialRepository, times(2)).save(any());
        ArgumentCaptor<Paquete> captor = ArgumentCaptor.forClass(Paquete.class);
        verify(paqueteRepository, times(2)).save(captor.capture());
        assertEquals(EstadoPaquete.ENTREGADO, captor.getAllValues().get(1).estadoActual());
    }

    @Test
    void sincronizarEstado_paqueteNoEncontrado_registraErrorYNoInsertaHistorial() throws Exception {
        when(gestionPaquetePort.consultarEstado(idRuta, idPaquete))
                .thenReturn(CompletableFuture.failedFuture(new PaqueteNoEncontradoException(idPaquete)));

        SincronizacionResultadoDTO resultado = service.sincronizarEstado(idRuta, idPaquete);

        assertEquals("PAQUETE_NO_ENCONTRADO", resultado.estado());
        verify(logRepository).save(any());
        verify(historialRepository, never()).save(any());
    }

    @Test
    void sincronizarEstado_timeout_marcaPendienteSincronizacion() throws Exception {
        when(gestionPaquetePort.consultarEstado(idRuta, idPaquete))
                .thenReturn(CompletableFuture.failedFuture(
                        new PendienteSincronizacionException(idPaquete, new RuntimeException("timeout"))));

        SincronizacionResultadoDTO resultado = service.sincronizarEstado(idRuta, idPaquete);

        assertEquals("PENDIENTE_SINCRONIZACION", resultado.estado());
        verify(logRepository).save(any());
        ArgumentCaptor<Paquete> captor = ArgumentCaptor.forClass(Paquete.class);
        verify(paqueteRepository).save(captor.capture());
        assertEquals(EstadoPaquete.PENDIENTE_SINCRONIZACION, captor.getValue().estadoActual());
    }

    @Test
    void sincronizarEstado_estadoNoMapeado_omiteCalculoYRegistraLog() throws Exception {
        GestionPaquete dto = new GestionPaquete(idPaquete.toString(), "EN_INSPECCION");
        when(gestionPaquetePort.consultarEstado(idRuta, idPaquete))
                .thenReturn(CompletableFuture.completedFuture(dto));

        SincronizacionResultadoDTO resultado = service.sincronizarEstado(idRuta, idPaquete);

        assertEquals("ESTADO_NO_MAPEADO", resultado.estado());
        assertNull(resultado.porcentajePago());
        verify(logRepository).save(any());
        verify(historialRepository, never()).save(any());
    }

    @Test
    void sincronizarEstado_idPaqueteNoCoincide_marcaPendiente() throws Exception {
        GestionPaquete dto = new GestionPaquete(UUID.randomUUID().toString(), "ENTREGADO");
        when(gestionPaquetePort.consultarEstado(idRuta, idPaquete))
                .thenReturn(CompletableFuture.completedFuture(dto));

        SincronizacionResultadoDTO resultado = service.sincronizarEstado(idRuta, idPaquete);

        assertEquals("PENDIENTE_SINCRONIZACION", resultado.estado());
        verify(paqueteRepository).save(any());
        verify(historialRepository, never()).save(any());
    }
}
