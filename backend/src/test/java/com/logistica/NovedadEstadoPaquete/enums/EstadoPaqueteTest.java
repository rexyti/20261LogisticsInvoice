package com.logistica.NovedadEstadoPaquete.enums;

import com.logistica.NovedadEstadoPaquete.domain.enums.EstadoPaquete;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * T008 – Unit test: verifica que cada estado del Enum mapea al porcentaje correcto.
 */
class EstadoPaqueteTest {

    @Test
    void entregado_debe_retornar_100_porciento() {
        assertThat(EstadoPaquete.ENTREGADO.getPorcentajePago()).isEqualTo(100);
    }

    @Test
    void devuelto_debe_retornar_50_porciento() {
        assertThat(EstadoPaquete.DEVUELTO.getPorcentajePago()).isEqualTo(50);
    }

    @Test
    void danado_debe_retornar_0_porciento() {
        assertThat(EstadoPaquete.DANADO.getPorcentajePago()).isEqualTo(0);
    }

    @Test
    void extraviado_debe_retornar_0_porciento() {
        assertThat(EstadoPaquete.EXTRAVIADO.getPorcentajePago()).isEqualTo(0);
    }

    @Test
    void fromString_mapea_ENTREGADO() {
        assertThat(EstadoPaquete.fromString("ENTREGADO")).contains(EstadoPaquete.ENTREGADO);
    }

    @Test
    void fromString_mapea_DANADO_con_tilde() {
        assertThat(EstadoPaquete.fromString("DAÑADO")).contains(EstadoPaquete.DANADO);
    }

    @Test
    void fromString_estado_desconocido_retorna_empty() {
        Optional<EstadoPaquete> resultado = EstadoPaquete.fromString("EN_INSPECCION");
        assertThat(resultado).isEmpty();
    }

    @Test
    void fromString_null_retorna_empty() {
        assertThat(EstadoPaquete.fromString(null)).isEmpty();
    }
}
