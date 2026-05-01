package com.logistica.contratos.application.usecases.contrato;

import com.logistica.contratos.application.dtos.request.ContratoRequestDTO;
import com.logistica.contratos.application.dtos.request.SeguroRequestDTO;
import com.logistica.contratos.application.dtos.response.ContratoResponseDTO;
import com.logistica.contratos.application.mappers.ContratoResponseMapper;
import com.logistica.contratos.application.usecases.contrato.CrearContratoUseCase;
import com.logistica.contratos.domain.enums.ContratosTipoVehiculo;
import com.logistica.contratos.domain.exceptions.ContratoYaExisteException;
import com.logistica.contratos.domain.exceptions.TransportistaNotFoundException;
import com.logistica.contratos.domain.models.ContratosContrato;
import com.logistica.contratos.domain.models.Seguro;
import com.logistica.contratos.domain.models.ContratosTransportista;
import com.logistica.contratos.domain.repositories.ContratosContratoRepository;
import com.logistica.contratos.domain.repositories.ContratosTransportistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearContratoUseCaseTest {

    @Mock
    private ContratosContratoRepository contratoRepository;
    @Mock
    private ContratosTransportistaRepository transportistaRepository;
    @Mock
    private ContratoResponseMapper responseMapper;

    @InjectMocks
    private CrearContratoUseCase useCase;

    private ContratoRequestDTO dtoValido;
    private UUID transportistaId;

    @BeforeEach
    void setUp() {
        transportistaId = UUID.randomUUID();

        SeguroRequestDTO seguro = new SeguroRequestDTO();
        seguro.setNumeroPoliza("POL-001");
        seguro.setEstado("VIGENTE");

        dtoValido = new ContratoRequestDTO();
        dtoValido.setIdContrato("CONT-001");
        dtoValido.setTipoContrato("MENSAJERIA");
        dtoValido.setTransportistaId(transportistaId);
        dtoValido.setEsPorParada(true);
        dtoValido.setPrecioParadas(new BigDecimal("15.50"));
        dtoValido.setTipoVehiculo(ContratosTipoVehiculo.VAN);
        dtoValido.setFechaInicio(LocalDateTime.of(2026, 1, 1, 0, 0));
        dtoValido.setFechaFinal(LocalDateTime.of(2026, 12, 31, 0, 0));
        dtoValido.setSeguro(seguro);
    }

    @Test
    @DisplayName("Registra el contrato cuando los datos son válidos")
    void debeRegistrarContratoConDatosValidos() {
        ContratosTransportista transportista = ContratosTransportista.builder()
                .transportistaId(transportistaId)
                .nombre("Juan Pérez")
                .build();

        ContratosContrato contratoCreado = ContratosContrato.builder()
                .id(UUID.randomUUID())
                .idContrato("CONT-001")
                .transportista(transportista)
                .tipoVehiculo(ContratosTipoVehiculo.VAN)
                .esPorParada(true)
                .precioParadas(new BigDecimal("15.50"))
                .fechaInicio(LocalDateTime.of(2026, 1, 1, 0, 0))
                .fechaFinal(LocalDateTime.of(2026, 12, 31, 0, 0))
                .seguro(Seguro.builder()
                        .idSeguro(UUID.randomUUID())
                        .numeroPoliza("POL-001")
                        .estado("VIGENTE")
                        .build())
                .build();

        ContratoResponseDTO responseEsperado = ContratoResponseDTO.builder()
                .id(contratoCreado.getId())
                .idContrato("CONT-001")
                .build();

        when(contratoRepository.existePorIdContrato("CONT-001")).thenReturn(false);
        when(transportistaRepository.buscarPorId(transportistaId)).thenReturn(Optional.of(transportista));
        when(contratoRepository.guardar(any())).thenReturn(contratoCreado);
        when(responseMapper.toResponseDTO(any())).thenReturn(responseEsperado);

        ContratoResponseDTO resultado = useCase.ejecutar(dtoValido);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdContrato()).isEqualTo("CONT-001");
        verify(contratoRepository).guardar(any());
    }

    @Test
    @DisplayName("Lanza excepción cuando el idContrato ya existe")
    void debeLanzarExcepcionCuandoContratoYaExiste() {
        when(contratoRepository.existePorIdContrato("CONT-001")).thenReturn(true);

        assertThatThrownBy(() -> useCase.ejecutar(dtoValido))
                .isInstanceOf(ContratoYaExisteException.class);

        verify(contratoRepository, never()).guardar(any());
        verify(transportistaRepository, never()).buscarPorId(any());
    }

    @Test
    @DisplayName("Lanza excepción cuando el transportista no existe")
    void debeLanzarExcepcionCuandoTransportistaNoeExiste() {
        when(contratoRepository.existePorIdContrato("CONT-001")).thenReturn(false);
        when(transportistaRepository.buscarPorId(transportistaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ejecutar(dtoValido))
                .isInstanceOf(TransportistaNotFoundException.class);

        verify(contratoRepository, never()).guardar(any());
    }
}
