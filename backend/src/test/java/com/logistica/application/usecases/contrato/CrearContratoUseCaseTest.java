package com.logistica.application.usecases.contrato;

import com.logistica.application.dtos.request.ContratoRequestDTO;
import com.logistica.application.dtos.response.ContratoResponseDTO;
import com.logistica.domain.enums.TipoContrato;
import com.logistica.domain.exceptions.ContratoYaExisteException;
import com.logistica.domain.models.Contrato;
import com.logistica.domain.models.Seguro;
import com.logistica.domain.models.Usuario;
import com.logistica.domain.models.Vehiculo;
import com.logistica.domain.repositories.ContratoRepository;
import com.logistica.domain.repositories.SeguroRepository;
import com.logistica.domain.repositories.UsuarioRepository;
import com.logistica.domain.repositories.VehiculoRepository;
import com.logistica.infrastructure.adapters.ContratoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearContratoUseCaseTest {

    @Mock
    private ContratoRepository contratoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private VehiculoRepository vehiculoRepository;
    @Mock
    private SeguroRepository seguroRepository;
    @Mock
    private ContratoMapper contratoMapper;

    @InjectMocks
    private CrearContratoUseCase useCase;

    private ContratoRequestDTO dtoValido;

    @BeforeEach
    void setUp() {
        dtoValido = ContratoRequestDTO.builder()
                .idContrato("CONT-001")
                .tipoContrato(TipoContrato.POR_PARADA)
                .nombreConductor("Juan Pérez")
                .precioParadas(new BigDecimal("15.50"))
                .tipoVehiculo("CAMION")
                .fechaInicio(LocalDate.of(2026, 1, 1))
                .fechaFinal(LocalDate.of(2026, 12, 31))
                .estadoSeguro("VIGENTE")
                .build();
    }

    @Test
    @DisplayName("Registra el contrato cuando los datos son válidos")
    void debeRegistrarContratoConDatosValidos() {
        Usuario usuarioGuardado = Usuario.builder().idUsuario(1L).nombre("Juan Pérez").build();
        Vehiculo vehiculoGuardado = Vehiculo.builder().idVehiculo(1L).idUsuario(1L).tipo("CAMION").build();
        Seguro seguroGuardado = Seguro.builder().idSeguro(1L).idUsuario(1L).estado("VIGENTE").build();
        Contrato contratoCreado = Contrato.builder().id(1L).idContrato("CONT-001").build();
        ContratoResponseDTO responseEsperado = ContratoResponseDTO.builder().id(1L).idContrato("CONT-001").build();

        when(contratoRepository.existePorIdContrato("CONT-001")).thenReturn(false);
        when(usuarioRepository.guardar(any())).thenReturn(usuarioGuardado);
        when(vehiculoRepository.guardar(any())).thenReturn(vehiculoGuardado);
        when(seguroRepository.guardar(any())).thenReturn(seguroGuardado);
        when(contratoMapper.toDomain(any(), anyLong(), anyLong())).thenReturn(contratoCreado);
        when(contratoRepository.guardar(any())).thenReturn(contratoCreado);
        when(contratoMapper.toResponseDTO(any())).thenReturn(responseEsperado);

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
                .isInstanceOf(ContratoYaExisteException.class)
                .hasMessageContaining("CONT-001");

        verify(contratoRepository, never()).guardar(any());
        verify(usuarioRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Crea usuario, vehículo y seguro de manera atómica")
    void debeCriarTodasLasEntidadesAsociadas() {
        Usuario usuarioGuardado = Usuario.builder().idUsuario(2L).nombre("Juan Pérez").build();
        Vehiculo vehiculoGuardado = Vehiculo.builder().idVehiculo(2L).idUsuario(2L).tipo("CAMION").build();
        Contrato contratoCreado = Contrato.builder().id(1L).idContrato("CONT-001").build();

        when(contratoRepository.existePorIdContrato(anyString())).thenReturn(false);
        when(usuarioRepository.guardar(any())).thenReturn(usuarioGuardado);
        when(vehiculoRepository.guardar(any())).thenReturn(vehiculoGuardado);
        when(seguroRepository.guardar(any())).thenReturn(Seguro.builder().idSeguro(1L).build());
        when(contratoMapper.toDomain(any(), anyLong(), anyLong())).thenReturn(contratoCreado);
        when(contratoRepository.guardar(any())).thenReturn(contratoCreado);
        when(contratoMapper.toResponseDTO(any())).thenReturn(ContratoResponseDTO.builder().build());

        useCase.ejecutar(dtoValido);

        verify(usuarioRepository).guardar(any());
        verify(vehiculoRepository).guardar(any());
        verify(seguroRepository).guardar(any());
    }
}
