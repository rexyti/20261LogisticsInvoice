package com.logistica.VisualizarLiquidación.application.usecases;

import com.logistica.application.visualizarLiquidacion.dtos.request.VisualizarLiquidacionFiltroDTO;
import com.logistica.application.visualizarLiquidacion.dtos.response.VisualizarLiquidacionDetalleDTO;
import com.logistica.application.visualizarLiquidacion.mappers.VisualizarLiquidacionDTOMapper;
import com.logistica.application.visualizarLiquidacion.security.VisualizarLiquidacionUsuarioAutenticado;
import com.logistica.application.visualizarLiquidacion.usecases.liquidacion.VisualizarLiquidacionBuscarUseCase;
import com.logistica.domain.visualizarLiquidacion.enums.EstadoLiquidacion;
import com.logistica.domain.visualizarLiquidacion.exceptions.LiquidacionAunNoCalculadaException;
import com.logistica.domain.visualizarLiquidacion.exceptions.LiquidacionNoEncontradaException;
import com.logistica.domain.visualizarLiquidacion.models.Liquidacion;
import com.logistica.domain.visualizarLiquidacion.models.ResultadoBusquedaPorRuta;
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
class BuscarLiquidacionesUseCaseTest {

    @Mock
    private LiquidacionRepository repository;

    @Mock
    private VisualizarLiquidacionDTOMapper mapper;

    @InjectMocks
    private VisualizarLiquidacionBuscarUseCase useCase;

    private UUID idRuta;
    private UUID idLiquidacion;
    private Liquidacion liquidacion;
    private VisualizarLiquidacionDetalleDTO detalleEsperado;
    private final String propietario = "transportista-456";

    @BeforeEach
    void setUp() {
        idRuta = UUID.randomUUID();
        idLiquidacion = UUID.randomUUID();

        Ruta ruta = Ruta.builder()
                .id(idRuta)
                .fechaInicio(LocalDateTime.now().minusDays(3))
                .fechaCierre(LocalDateTime.now().minusDays(2))
                .tipoVehiculo("MOTO")
                .precioParada(new BigDecimal("8000"))
                .numeroParadas(10)
                .build();

        liquidacion = Liquidacion.builder()
                .id(idLiquidacion)
                .idRuta(idRuta)
                .estadoLiquidacion(EstadoLiquidacion.CALCULADA)
                .montoBruto(new BigDecimal("80000"))
                .montoNeto(new BigDecimal("78000"))
                .fechaCalculo(LocalDateTime.now())
                .usuarioId(propietario)
                .ruta(ruta)
                .ajustes(List.of())
                .build();

        detalleEsperado = new VisualizarLiquidacionDetalleDTO(
                idLiquidacion, null, idRuta,
                ruta.getFechaInicio(), ruta.getFechaCierre(),
                "MOTO", new BigDecimal("8000"), 10,
                new BigDecimal("80000"), new BigDecimal("78000"),
                "CALCULADA", liquidacion.getFechaCalculo(), propietario, List.of());
    }

    @Test
    void dadoIdRutaValido_cuandoBuscar_retornaLiquidacionCorrecta() {
        VisualizarLiquidacionFiltroDTO filtro = new VisualizarLiquidacionFiltroDTO();
        filtro.setIdRuta(idRuta);

        when(repository.buscarPorIdRuta(idRuta))
                .thenReturn(new ResultadoBusquedaPorRuta.Encontrada(liquidacion));
        when(mapper.toDetalle(liquidacion)).thenReturn(detalleEsperado);

        VisualizarLiquidacionUsuarioAutenticado gestor = usuarioConRol("ROLE_GESTOR_FINANCIERO", "gestor-001");
        VisualizarLiquidacionDetalleDTO resultado = useCase.ejecutar(filtro, gestor);

        assertThat(resultado.idRuta()).isEqualTo(idRuta);
        assertThat(resultado.tipoVehiculo()).isEqualTo("MOTO");
        assertThat(resultado.numeroParadas()).isEqualTo(10);
    }

    @Test
    void dadoIdLiquidacionValido_cuandoBuscar_retornaDetalleCorrecto() {
        VisualizarLiquidacionFiltroDTO filtro = new VisualizarLiquidacionFiltroDTO();
        filtro.setIdLiquidacion(idLiquidacion);

        when(repository.buscarPorId(idLiquidacion)).thenReturn(Optional.of(liquidacion));
        when(mapper.toDetalle(liquidacion)).thenReturn(detalleEsperado);

        VisualizarLiquidacionUsuarioAutenticado gestor = usuarioConRol("ROLE_GESTOR_FINANCIERO", "gestor-001");
        VisualizarLiquidacionDetalleDTO resultado = useCase.ejecutar(filtro, gestor);

        assertThat(resultado.idLiquidacion()).isEqualTo(idLiquidacion);
    }

    @Test
    void dadoIdRutaCompletamenteInexistente_cuandoBuscar_lanzaLiquidacionNoEncontradaException() {
        UUID idRutaInexistente = UUID.randomUUID();
        VisualizarLiquidacionFiltroDTO filtro = new VisualizarLiquidacionFiltroDTO();
        filtro.setIdRuta(idRutaInexistente);

        when(repository.buscarPorIdRuta(idRutaInexistente))
                .thenReturn(new ResultadoBusquedaPorRuta.RutaNoExiste());

        VisualizarLiquidacionUsuarioAutenticado gestor = usuarioConRol("ROLE_GESTOR_FINANCIERO", "gestor-001");

        assertThatThrownBy(() -> useCase.ejecutar(filtro, gestor))
                .isInstanceOf(LiquidacionNoEncontradaException.class);
    }

    @Test
    void dadoIdRutaSinLiquidacionCalculada_cuandoBuscar_lanzaLiquidacionAunNoCalculadaException() {
        UUID idRutaExistente = UUID.randomUUID();
        VisualizarLiquidacionFiltroDTO filtro = new VisualizarLiquidacionFiltroDTO();
        filtro.setIdRuta(idRutaExistente);

        when(repository.buscarPorIdRuta(idRutaExistente))
                .thenReturn(new ResultadoBusquedaPorRuta.RutaSinLiquidacion());

        VisualizarLiquidacionUsuarioAutenticado gestor = usuarioConRol("ROLE_GESTOR_FINANCIERO", "gestor-001");

        assertThatThrownBy(() -> useCase.ejecutar(filtro, gestor))
                .isInstanceOf(LiquidacionAunNoCalculadaException.class)
                .hasMessageContaining("aun no posee");
    }

    private VisualizarLiquidacionUsuarioAutenticado usuarioConRol(String rol, String usuarioId) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(usuarioId);
        when(auth.getAuthorities()).thenAnswer(inv -> List.of(new SimpleGrantedAuthority(rol)));
        return VisualizarLiquidacionUsuarioAutenticado.from(auth);
    }
}
