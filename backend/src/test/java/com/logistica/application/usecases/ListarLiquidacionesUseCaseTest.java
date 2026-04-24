package com.logistica.application.usecases;

import com.logistica.application.dtos.response.LiquidacionListItemDTO;
import com.logistica.application.dtos.response.LiquidacionListResponseDTO;
import com.logistica.application.security.UsuarioAutenticado;
import com.logistica.application.usecases.liquidacion.ListarLiquidacionesUseCase;
import com.logistica.domain.enums.EstadoLiquidacion;
import com.logistica.domain.models.Liquidacion;
import com.logistica.domain.models.Ruta;
import com.logistica.domain.repositories.LiquidacionRepository;
import com.logistica.application.mappers.LiquidacionDTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// T011 — usuario autorizado puede consultar listado paginado de liquidaciones
// T017 — usuario sin permiso global solo ve sus propias liquidaciones
// T018 — gestor financiero ve todas las liquidaciones autorizadas
@ExtendWith(MockitoExtension.class)
class ListarLiquidacionesUseCaseTest {

    @Mock
    private LiquidacionRepository repository;

    @Mock
    private LiquidacionDTOMapper mapper;

    @InjectMocks
    private ListarLiquidacionesUseCase useCase;

    private Liquidacion liquidacionA;
    private Liquidacion liquidacionB;
    private final String usuarioA = "usuario-a";
    private final String usuarioB = "usuario-b";

    @BeforeEach
    void setUp() {
        Ruta ruta = Ruta.builder()
                .id(UUID.randomUUID())
                .fechaInicio(LocalDateTime.now().minusDays(2))
                .fechaCierre(LocalDateTime.now().minusDays(1))
                .tipoVehiculo("CAMION")
                .precioParada(new BigDecimal("15000"))
                .numeroParadas(5)
                .build();

        liquidacionA = Liquidacion.builder()
                .id(UUID.randomUUID())
                .idRuta(ruta.getId())
                .estadoLiquidacion(EstadoLiquidacion.CALCULADA)
                .montoBruto(new BigDecimal("75000"))
                .montoNeto(new BigDecimal("70000"))
                .fechaCalculo(LocalDateTime.now())
                .usuarioId(usuarioA)
                .ruta(ruta)
                .ajustes(List.of())
                .build();

        liquidacionB = Liquidacion.builder()
                .id(UUID.randomUUID())
                .idRuta(UUID.randomUUID())
                .estadoLiquidacion(EstadoLiquidacion.CALCULADA)
                .montoBruto(new BigDecimal("50000"))
                .montoNeto(new BigDecimal("48000"))
                .fechaCalculo(LocalDateTime.now())
                .usuarioId(usuarioB)
                .ruta(ruta)
                .ajustes(List.of())
                .build();
    }

    // T011 / T018: gestor financiero (permiso global) recibe todas las liquidaciones
    @Test
    void dadoGestorFinanciero_cuandoListar_retornaTodasLasLiquidaciones() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Liquidacion> todas = List.of(liquidacionA, liquidacionB);
        when(repository.listarTodas(pageable)).thenReturn(new PageImpl<>(todas, pageable, 2));
        when(mapper.toListItem(any())).thenAnswer(inv -> itemDesde(inv.getArgument(0)));

        LiquidacionListResponseDTO respuesta = useCase.ejecutar(pageable, buildUsuario("ROLE_GESTOR_FINANCIERO", "gestor-1"));

        assertThat(respuesta.contenido()).hasSize(2);
        assertThat(respuesta.totalElementos()).isEqualTo(2);
        verify(repository).listarTodas(pageable);
        verify(repository, never()).listarPorUsuario(any(), any());
    }

    // T017: transportista sin permiso global solo ve sus propias liquidaciones
    @Test
    void dadoTransportistaSinPermisoGlobal_cuandoListar_soloRetornaSusLiquidaciones() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.listarPorUsuario(usuarioA, pageable))
                .thenReturn(new PageImpl<>(List.of(liquidacionA), pageable, 1));
        when(mapper.toListItem(liquidacionA)).thenReturn(itemDesde(liquidacionA));

        LiquidacionListResponseDTO respuesta = useCase.ejecutar(pageable, buildUsuario("ROLE_TRANSPORTISTA", usuarioA));

        assertThat(respuesta.contenido()).hasSize(1);
        assertThat(respuesta.contenido().get(0).idLiquidacion()).isEqualTo(liquidacionA.getId());
        verify(repository).listarPorUsuario(usuarioA, pageable);
        verify(repository, never()).listarTodas(any());
    }

    // T011: metadatos de paginacion se propagan correctamente en la respuesta
    @Test
    void dadoGestorFinanciero_cuandoListarSegundaPagina_retornaPaginacionCorrecta() {
        Pageable pageable = PageRequest.of(1, 5);
        when(repository.listarTodas(pageable))
                .thenReturn(new PageImpl<>(List.of(liquidacionA), pageable, 6));
        when(mapper.toListItem(any())).thenReturn(itemDesde(liquidacionA));

        LiquidacionListResponseDTO respuesta = useCase.ejecutar(pageable, buildUsuario("ROLE_GESTOR_FINANCIERO", "gestor-1"));

        assertThat(respuesta.pagina()).isEqualTo(1);
        assertThat(respuesta.tamano()).isEqualTo(5);
        assertThat(respuesta.totalElementos()).isEqualTo(6);
        assertThat(respuesta.totalPaginas()).isEqualTo(2);
        assertThat(respuesta.esUltima()).isTrue();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private UsuarioAutenticado buildUsuario(String rol, String userId) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(userId);
        when(auth.getAuthorities()).thenAnswer(inv -> List.of(new SimpleGrantedAuthority(rol)));
        return UsuarioAutenticado.from(auth);
    }

    private LiquidacionListItemDTO itemDesde(Liquidacion liq) {
        return new LiquidacionListItemDTO(
                liq.getId(), liq.getIdRuta(), null, null, null, null, null,
                liq.getMontoBruto(), liq.getMontoNeto(),
                liq.getEstadoLiquidacion().name(), liq.getFechaCalculo(), List.of());
    }
}
