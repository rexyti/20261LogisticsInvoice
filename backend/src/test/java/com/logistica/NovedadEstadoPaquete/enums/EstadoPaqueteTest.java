package com.logistica.NovedadEstadoPaquete.enums;

import com.logistica.domain.novedadEstadoPaquete.enums.NovedadEstadoPaqueteEstadoPaquete;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoPaqueteTest {

    @Test
    void entregado_debe_retornar_100_porciento() {
        assertThat(NovedadEstadoPaqueteEstadoPaquete.ENTREGADO.getPorcentajePago()).isEqualTo(100);
    }

    @Test
    void devuelto_debe_retornar_50_porciento() {
        assertThat(NovedadEstadoPaqueteEstadoPaquete.DEVUELTO.getPorcentajePago()).isEqualTo(50);
    }

    @Test
    void danado_debe_retornar_0_porciento() {
        assertThat(NovedadEstadoPaqueteEstadoPaquete.DANADO.getPorcentajePago()).isEqualTo(0);
    }

    @Test
    void extraviado_debe_retornar_0_porciento() {
        assertThat(NovedadEstadoPaqueteEstadoPaquete.EXTRAVIADO.getPorcentajePago()).isEqualTo(0);
    }

    @Test
    void fromString_mapea_ENTREGADO() {
        assertThat(NovedadEstadoPaqueteEstadoPaquete.fromString("ENTREGADO")).contains(NovedadEstadoPaqueteEstadoPaquete.ENTREGADO);
    }

    @Test
    void fromString_mapea_DANADO_con_tilde() {
        assertThat(NovedadEstadoPaqueteEstadoPaquete.fromString("DAÑADO")).contains(NovedadEstadoPaqueteEstadoPaquete.DANADO);
    }

    @Test
    void fromString_estado_desconocido_retorna_empty() {
        Optional<NovedadEstadoPaqueteEstadoPaquete> resultado = NovedadEstadoPaqueteEstadoPaquete.fromString("EN_INSPECCION");
        assertThat(resultado).isEmpty();
    }

    @Test
    void fromString_null_retorna_empty() {
        assertThat(NovedadEstadoPaqueteEstadoPaquete.fromString(null)).isEmpty();
    }
}
