package com.logistica.application.dtos;

import com.logistica.application.dtos.request.RutaCerradaEventDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * T010 — Verifica que el JSON del contrato se deserializa correctamente al DTO,
 * incluyendo que el campo paradas se mapea como List y no como objeto único.
 */
@JsonTest
class RutaCerradaEventDTOMappingTest {

    @Autowired
    private JacksonTester<RutaCerradaEventDTO> json;

    @Test
    void deserializaEventoCompleto() throws IOException {
        var result = json.read("/evento_ruta_cerrada.json");

        assertThat(result).extractingJsonPathStringValue("$.tipo_evento").isEqualTo("RUTA_CERRADA");
        assertThat(result.getObject().getRutaId())
                .isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        assertThat(result.getObject().getConductor().getNombre()).isEqualTo("Juan Pérez");
        assertThat(result.getObject().getVehiculo().getTipo()).isEqualTo("MOTO");
    }

    @Test
    void paradasSeDeserializaComoLista() throws IOException {
        var result = json.read("/evento_ruta_cerrada.json");

        assertThat(result.getObject().getParadas()).hasSize(2);
        assertThat(result.getObject().getParadas().get(0).getEstado()).isEqualTo("FALLIDA");
        assertThat(result.getObject().getParadas().get(0).getMotivoNoEntrega()).isEqualTo("CLIENTE_AUSENTE");
        assertThat(result.getObject().getParadas().get(1).getEstado()).isEqualTo("EXITOSA");
        assertThat(result.getObject().getParadas().get(1).getMotivoNoEntrega()).isNull();
    }
}
