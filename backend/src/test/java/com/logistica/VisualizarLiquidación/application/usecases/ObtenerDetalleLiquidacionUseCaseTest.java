package com.logistica.VisualizarLiquidación.application.usecases;

import com.logistica.application.visualizarLiquidacion.dtos.response.VisualizarLiquidacionAjusteDTO;
import com.logistica.application.visualizarLiquidacion.dtos.response.VisualizarLiquidacionDetalleDTO;
import com.logistica.application.visualizarLiquidacion.mappers.VisualizarLiquidacionDTOMapper;
import com.logistica.application.visualizarLiquidacion.security.VisualizarLiquidacionUsuarioAutenticado;
import com.logistica.application.visualizarLiquidacion.usecases.liquidacion.VisualizarLiquidacionObtenerDetalleUseCase;
import com.logistica.domain.visualizarLiquidacion.enums.EstadoLiquidacion;
import com.logistica.domain.visualizarLiquidacion.exceptions.AccesoDenegadoException;
import com.logistica.domain.visualizarLiquidacion.exceptions.LiquidacionNoEncontradaException;
import com.logistica.domain.visualizarLiquidacion.models.Ajuste;
import com.logistica.domain.visualizarLiquidacion.models.Liquidacion;
import com.logistica.domain.visualizarLiquidacion.models.Ruta;
import com.logistica.domain.visualizarLiquidacion.repositories.LiquidacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObtenerDetalleLiquidacionUseCaseTest {

    @Mock
    private LiquidacionRepository repository;

    @Mock
    private VisualizarLiquidacionDTOMapper mapper;

    @InjectMocks
    private VisualizarLiquidacionObtenerDetalleUseCase useCase;

    private UUID idLiquidacion;
    private Liquidacion liquidacion;
    private VisualizarLiquidacionDetalleDTO detalleEsperado;
    private final String propietario = "transportista-123";

    @BeforeEach
    void setUp() {
        idLiquidacion = UUID.randomUUID();
        UUID idRuta = UUID.randomUUID();
        UUID idContrato = UUID.randomUUID();

        Ruta ruta = Ruta.builder()
                .id(idRuta)
                .fechaInicio(LocalDateTime.of(2026, 1, 10, 8, 0))
                .fechaCierre(LocalDateTime.of(2026, 1, 10, 18, 0))
                .tipoVehiculo("CAMION")
                .precioParada(new BigDecimal("20000"))
                .numeroParadas(4)
                .build();

        Ajuste ajuste = Ajuste.builder()
                .id(UUID.randomUUID())
                .tipo("PENALIZACION")
                .monto(new BigDecimal("5000"))
                .razon("Paquete danado")
                .build();

        liquidacion = Liquidacion.builder()
                .id(idLiquidacion)
                .idRuta(idRuta)
                .idContrato(idContrato)
                .estadoLiquidacion(EstadoLiquidacion.CALCULADA)
                .montoBruto(new BigDecimal("80000"))
                .montoNeto(new BigDecimal("75000"))
                .fechaCalculo(LocalDateTime.of(2026, 1, 11, 9, 0))
                .usuarioId(propietario)
                .ruta(ruta)
                .ajustes(List.of(ajuste))
                .build();

        detalleEsperado = new VisualizarLiquidacionDetalleDTO(
                idLiquidacion, idContrato, idRuta,
                ruta.getFechaInicio(), ruta.getFechaCierre(),
                "CAMION", new BigDecimal("20000"), 4,
                new BigDecimal("80000"), new BigDecimal("75000"),
                "CALCULADA", liquidacion.getFechaCalculo(), propietario,
                List.of(new VisualizarLiquidacionAjusteDTO(ajuste.getId(), "PENALIZACION",
                        new BigDecimal("5000"), "Paquete danado")));
    }

    @Test
    void dadoPropietario_cuandoObtenerDetalle_retornaDetalleCompleto() {
        when(repository.buscarPorId(idLiquidacion)).thenReturn(Optional.of(liquidacion));
        when(mapper.toDetalle(liquidacion)).thenReturn(detalleEsperado);

        VisualizarLiquidacionUsuarioAutenticado propietarioUser = usuarioConRol("ROLE_TRANSPORTISTA", propietario);
        VisualizarLiquidacionDetalleDTO resultado = useCase.ejecutar(idLiquidacion, propietarioUser);

        assertThat(resultado.idLiquidacion()).isEqualTo(idLiquidacion);
        assertThat(resultado.estadoLiquidacion()).isEqualTo("CALCULADA");
        assertThat(resultado.ajustes()).hasSize(1);
        assertThat(resultado.ajustes().get(0).tipo()).isEqualTo("PENALIZACION");
        assertThat(resultado.montoBruto()).isEqualByComparingTo("80000");
        assertThat(resultado.montoNeto()).isEqualByComparingTo("75000");
        assertThat(resultado.tipoVehiculo()).isEqualTo("CAMION");
        assertThat(resultado.numeroParadas()).isEqualTo(4);
    }

    @Test
    void dadoGestorFinanciero_cuandoObtenerDetalle_retornaDetalleSinRestriccion() {
        when(repository.buscarPorId(idLiquidacion)).thenReturn(Optional.of(liquidacion));
        when(mapper.toDetalle(liquidacion)).thenReturn(detalleEsperado);

        VisualizarLiquidacionUsuarioAutenticado gestor = usuarioConRol("ROLE_GESTOR_FINANCIERO", "gestor-999");
        VisualizarLiquidacionDetalleDTO resultado = useCase.ejecutar(idLiquidacion, gestor);

        assertThat(resultado).isNotNull();
        assertThat(resultado.usuarioId()).isEqualTo(propietario);
    }

    @Test
    void dadoIdInexistente_cuandoObtenerDetalle_lanzaLiquidacionNoEncontradaException() {
        UUID idInexistente = UUID.randomUUID();
        when(repository.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        VisualizarLiquidacionUsuarioAutenticado gestor = usuarioConRol("ROLE_GESTOR_FINANCIERO", "gestor-999");

        assertThatThrownBy(() -> useCase.ejecutar(idInexistente, gestor))
                .isInstanceOf(LiquidacionNoEncontradaException.class)
                .hasMessageContaining(idInexistente.toString());
    }

    @Test
    void dadoTransportistaAjeno_cuandoObtenerDetalle_lanzaAccesoDenegadoException() {
        when(repository.buscarPorId(idLiquidacion)).thenReturn(Optional.of(liquidacion));

        VisualizarLiquidacionUsuarioAutenticado otro = usuarioConRol("ROLE_TRANSPORTISTA", "otro-transportista");

        assertThatThrownBy(() -> useCase.ejecutar(idLiquidacion, otro))
                .isInstanceOf(AccesoDenegadoException.class)
                .hasMessageContaining("permisos");
    }

    private VisualizarLiquidacionUsuarioAutenticado usuarioConRol(String rol, String usuarioId) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(usuarioId);
        when(auth.getAuthorities()).thenAnswer(inv -> List.of(new SimpleGrantedAuthority(rol)));
        return VisualizarLiquidacionUsuarioAutenticado.from(auth);
    }
}
