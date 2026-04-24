package com.logistica.application.usecases;

import com.logistica.application.dtos.request.FiltroLiquidacionDTO;
import com.logistica.application.dtos.response.LiquidacionDetalleDTO;
import com.logistica.application.mappers.LiquidacionDTOMapper;
import com.logistica.application.security.UsuarioAutenticado;
import com.logistica.application.usecases.liquidacion.BuscarLiquidacionesUseCase;
import com.logistica.domain.enums.EstadoLiquidacion;
import com.logistica.domain.exceptions.LiquidacionAunNoCalculadaException;
import com.logistica.domain.exceptions.LiquidacionNoEncontradaException;
import com.logistica.domain.models.Liquidacion;
import com.logistica.domain.models.ResultadoBusquedaPorRuta;
import com.logistica.domain.models.Ruta;
import com.logistica.domain.repositories.LiquidacionRepository;
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

// T014 — busqueda por idRuta retorna liquidacion correcta cuando existe correspondencia
// T015 — liquidacion inexistente retorna respuesta controlada (escenario 3)
// T016 — ruta sin liquidacion calculada retorna respuesta diferenciada (escenario 4)
@ExtendWith(MockitoExtension.class)
class BuscarLiquidacionesUseCaseTest {

    @Mock
    private LiquidacionRepository repository;

    @Mock
    private LiquidacionDTOMapper mapper;

    @InjectMocks
    private BuscarLiquidacionesUseCase useCase;

    private UUID idRuta;
    private UUID idLiquidacion;
    private Liquidacion liquidacion;
    private LiquidacionDetalleDTO detalleEsperado;
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

        detalleEsperado = new LiquidacionDetalleDTO(
                idLiquidacion, null, idRuta,
                ruta.getFechaInicio(), ruta.getFechaCierre(),
                "MOTO", new BigDecimal("8000"), 10,
                new BigDecimal("80000"), new BigDecimal("78000"),
                "CALCULADA", liquidacion.getFechaCalculo(), propietario, List.of());
    }

    // T014: busqueda por idRuta retorna la liquidacion cuando existe
    @Test
    void dadoIdRutaValido_cuandoBuscar_retornaLiquidacionCorrecta() {
        FiltroLiquidacionDTO filtro = new FiltroLiquidacionDTO();
        filtro.setIdRuta(idRuta);

        when(repository.buscarPorIdRuta(idRuta))
                .thenReturn(new ResultadoBusquedaPorRuta.Encontrada(liquidacion));
        when(mapper.toDetalle(liquidacion)).thenReturn(detalleEsperado);

        UsuarioAutenticado gestor = usuarioConRol("ROLE_GESTOR_FINANCIERO", "gestor-001");
        LiquidacionDetalleDTO resultado = useCase.ejecutar(filtro, gestor);

        assertThat(resultado.idRuta()).isEqualTo(idRuta);
        assertThat(resultado.tipoVehiculo()).isEqualTo("MOTO");
        assertThat(resultado.numeroParadas()).isEqualTo(10);
    }

    // T014: busqueda por idLiquidacion retorna la liquidacion cuando existe
    @Test
    void dadoIdLiquidacionValido_cuandoBuscar_retornaDetalleCorrecto() {
        FiltroLiquidacionDTO filtro = new FiltroLiquidacionDTO();
        filtro.setIdLiquidacion(idLiquidacion);

        when(repository.buscarPorId(idLiquidacion)).thenReturn(Optional.of(liquidacion));
        when(mapper.toDetalle(liquidacion)).thenReturn(detalleEsperado);

        UsuarioAutenticado gestor = usuarioConRol("ROLE_GESTOR_FINANCIERO", "gestor-001");
        LiquidacionDetalleDTO resultado = useCase.ejecutar(filtro, gestor);

        assertThat(resultado.idLiquidacion()).isEqualTo(idLiquidacion);
    }

    // T015 (escenario 3): no existe ninguna liquidacion ni ruta con ese idRuta
    @Test
    void dadoIdRutaCompletamenteInexistente_cuandoBuscar_lanzaLiquidacionNoEncontradaException() {
        UUID idRutaInexistente = UUID.randomUUID();
        FiltroLiquidacionDTO filtro = new FiltroLiquidacionDTO();
        filtro.setIdRuta(idRutaInexistente);

        when(repository.buscarPorIdRuta(idRutaInexistente))
                .thenReturn(new ResultadoBusquedaPorRuta.RutaNoExiste());

        UsuarioAutenticado gestor = usuarioConRol("ROLE_GESTOR_FINANCIERO", "gestor-001");

        assertThatThrownBy(() -> useCase.ejecutar(filtro, gestor))
                .isInstanceOf(LiquidacionNoEncontradaException.class);
    }

    // T016 (escenario 4): la ruta existe pero aun no tiene liquidacion calculada
    @Test
    void dadoIdRutaSinLiquidacionCalculada_cuandoBuscar_lanzaLiquidacionAunNoCalculadaException() {
        UUID idRutaExistente = UUID.randomUUID();
        FiltroLiquidacionDTO filtro = new FiltroLiquidacionDTO();
        filtro.setIdRuta(idRutaExistente);

        when(repository.buscarPorIdRuta(idRutaExistente))
                .thenReturn(new ResultadoBusquedaPorRuta.RutaSinLiquidacion());

        UsuarioAutenticado gestor = usuarioConRol("ROLE_GESTOR_FINANCIERO", "gestor-001");

        assertThatThrownBy(() -> useCase.ejecutar(filtro, gestor))
                .isInstanceOf(LiquidacionAunNoCalculadaException.class)
                .hasMessageContaining("aun no posee");
    }

    private UsuarioAutenticado usuarioConRol(String rol, String usuarioId) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(usuarioId);
        when(auth.getAuthorities()).thenAnswer(inv -> List.of(new SimpleGrantedAuthority(rol)));
        return UsuarioAutenticado.from(auth);
    }
}
