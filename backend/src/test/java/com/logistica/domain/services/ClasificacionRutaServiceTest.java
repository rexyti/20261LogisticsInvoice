package com.logistica.domain.services;

import com.logistica.domain.enums.MotivoFalla;
import com.logistica.domain.enums.ResponsableFalla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * T011 — Verifica que el servicio clasifica correctamente el responsable
 * de cada parada según su motivoNoEntrega (FR-002).
 */
class ClasificacionRutaServiceTest {

    private ClasificacionRutaService service;

    @BeforeEach
    void setUp() {
        service = new ClasificacionRutaService();
    }

    @ParameterizedTest(name = "{0} → {1}")
    @CsvSource({
            "CLIENTE_AUSENTE,   CLIENTE",
            "RECHAZADO,         CLIENTE",
            "DIRECCION_ERRONEA, CLIENTE",
            "PAQUETE_DANADO,    TRANSPORTISTA",
            "PERDIDA_PAQUETE,   TRANSPORTISTA",
            "ZONA_DIFICIL_ACCESO, EMPRESA"
    })
    void clasificaResponsableCorrectamente(String motivoNombre, String responsableEsperado) {
        MotivoFalla motivo = MotivoFalla.valueOf(motivoNombre);
        assertThat(motivo.getResponsable())
                .isEqualTo(ResponsableFalla.valueOf(responsableEsperado));
    }

    @ParameterizedTest(name = "JSON ''{0}'' → {1}")
    @CsvSource({
            "CLIENTE_AUSENTE,   CLIENTE",
            "RECHAZADO,         CLIENTE",
            "PAQUETE_DAÑADO,    TRANSPORTISTA",
            "PÉRDIDA_PAQUETE,   TRANSPORTISTA"
    })
    void clasificaDesdeValorJsonConAcentos(String valorJson, String responsableEsperado) {
        ResponsableFalla resultado = service.clasificarResponsable(valorJson);
        assertThat(resultado).isEqualTo(ResponsableFalla.valueOf(responsableEsperado));
    }
}
