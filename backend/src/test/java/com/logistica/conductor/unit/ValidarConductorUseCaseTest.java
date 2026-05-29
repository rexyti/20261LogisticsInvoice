package com.logistica.conductor.unit;

import com.logistica.application.conductor.dtos.response.ConductorResponseDTO;
import com.logistica.application.conductor.usecases.ValidarConductorUseCase;
import com.logistica.domain.conductor.enums.EstadoConductor;
import com.logistica.domain.conductor.exceptions.ConductorInactivoException;
import com.logistica.domain.conductor.exceptions.ConductorNoEncontradoException;
import com.logistica.domain.conductor.models.Conductor;
import com.logistica.domain.conductor.ports.ConductorGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidarConductorUseCaseTest {

    @Mock
    private ConductorGateway conductorGateway;

    @InjectMocks
    private ValidarConductorUseCase useCase;

    private static final UUID ID_CONDUCTOR = UUID.randomUUID();

    @Test
    void ejecutar_conductorActivo_retornaDTO() {
        Conductor conductor = Conductor.builder()
                .conductorId(ID_CONDUCTOR)
                .nombre("Juan Pérez")
                .estado(EstadoConductor.ACTIVO)
                .build();
        when(conductorGateway.consultar(ID_CONDUCTOR)).thenReturn(conductor);

        ConductorResponseDTO result = useCase.ejecutar(ID_CONDUCTOR);

        assertThat(result.getConductorId()).isEqualTo(ID_CONDUCTOR);
        assertThat(result.getNombre()).isEqualTo("Juan Pérez");
        assertThat(result.getEstado()).isEqualTo(EstadoConductor.ACTIVO);
    }

    @Test
    void ejecutar_conductorInactivo_lanzaConductorInactivoException() {
        Conductor conductor = Conductor.builder()
                .conductorId(ID_CONDUCTOR)
                .nombre("Pedro Inactivo")
                .estado(EstadoConductor.INACTIVO)
                .build();
        when(conductorGateway.consultar(ID_CONDUCTOR)).thenReturn(conductor);

        assertThatThrownBy(() -> useCase.ejecutar(ID_CONDUCTOR))
                .isInstanceOf(ConductorInactivoException.class);

        verify(conductorGateway).consultar(ID_CONDUCTOR);
    }

    @Test
    void ejecutar_conductorNoEncontrado_propagaExcepcion() {
        when(conductorGateway.consultar(ID_CONDUCTOR))
                .thenThrow(new ConductorNoEncontradoException(ID_CONDUCTOR));

        assertThatThrownBy(() -> useCase.ejecutar(ID_CONDUCTOR))
                .isInstanceOf(ConductorNoEncontradoException.class);
    }

    @Test
    void ejecutar_verificaLlamadaAlGateway() {
        Conductor conductor = Conductor.builder()
                .conductorId(ID_CONDUCTOR)
                .nombre("Test")
                .estado(EstadoConductor.ACTIVO)
                .build();
        when(conductorGateway.consultar(ID_CONDUCTOR)).thenReturn(conductor);

        useCase.ejecutar(ID_CONDUCTOR);

        verify(conductorGateway, times(1)).consultar(ID_CONDUCTOR);
        verifyNoMoreInteractions(conductorGateway);
    }
}
