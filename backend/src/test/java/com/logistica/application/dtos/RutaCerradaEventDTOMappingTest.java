package com.logistica.application.dtos;

import com.logistica.cierreRuta.application.dtos.request.CierreRutaRutaCerradaEventDTO;
import com.logistica.cierreRuta.domain.enums.EstadoParada;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RutaCerradaEventDTOMappingTest {

    @Autowired
    private JacksonTester<CierreRutaRutaCerradaEventDTO> json;


    private CierreRutaRutaCerradaEventDTO load() throws IOException {
        return json.read("/evento_ruta_cerrada.json").getObject();
    }


    @Test
    void deserializa_evento_completo_correctamente() throws IOException {

        var dto = load();

        assertThat(dto.getTipoEvento())
                .isEqualTo("RUTA_CERRADA");

        assertThat(dto.getRutaId())
                .isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));

        assertThat(dto.getFechaHoraInicioTransito())
                .isNotNull();

        assertThat(dto.getFechaHoraCierre())
                .isNotNull();

        assertThat(dto.getConductor())
                .isNotNull();

        assertThat(dto.getConductor().getNombre())
                .isEqualTo("Juan Pérez");

        assertThat(dto.getVehiculo())
                .isNotNull();

        assertThat(dto.getVehiculo().getTipo())
                .isEqualTo("MOTO");
    }


    @Test
    void paradas_se_deserializa_como_lista_correcta() throws IOException {

        var dto = load();

        assertThat(dto.getParadas())
                .isNotNull()
                .hasSize(2);

        var parada1 = dto.getParadas().get(0);
        assertThat(parada1.getEstado()).isEqualTo(EstadoParada.FALLIDA);
        assertThat(parada1.getMotivoNoEntrega()).isEqualTo("CLIENTE_AUSENTE");

        var parada2 = dto.getParadas().get(1);
        assertThat(parada2.getEstado()).isEqualTo(EstadoParada.EXITOSA);
        assertThat(parada2.getMotivoNoEntrega()).isNull();
    }
}
