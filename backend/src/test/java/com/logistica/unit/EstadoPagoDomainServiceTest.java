package com.logistica.unit;

import com.logistica.domain.enums.EstadoPagoEnum;
import com.logistica.domain.services.EstadoPagoDomainService;
import com.logistica.shared.exceptions.EstadoPagoInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoPagoDomainServiceTest {

    private EstadoPagoDomainService service;

    @BeforeEach
    void setUp() {
        service = new EstadoPagoDomainService();
    }

    @Test
    void estadoFinal_PAGADO_esTrue() {
        assertThat(service.esEstadoFinal(EstadoPagoEnum.PAGADO)).isTrue();
    }

    @Test
    void estadoFinal_RECHAZADO_esTrue() {
        assertThat(service.esEstadoFinal(EstadoPagoEnum.RECHAZADO)).isTrue();
    }

    @Test
    void estadoFinal_PENDIENTE_esFalse() {
        assertThat(service.esEstadoFinal(EstadoPagoEnum.PENDIENTE)).isFalse();
    }

    @Test
    void estadoFinal_EN_PROCESO_esFalse() {
        assertThat(service.esEstadoFinal(EstadoPagoEnum.EN_PROCESO)).isFalse();
    }

    @Test
    void validarEstadoConocido_conNull_lanzaExcepcion() {
        assertThatThrownBy(() -> service.validarEstadoConocido(null))
                .isInstanceOf(EstadoPagoInvalidoException.class);
    }

    @Test
    void validarEstadoConocido_conEstadoValido_noLanzaExcepcion() {
        for (EstadoPagoEnum estado : EstadoPagoEnum.values()) {
            service.validarEstadoConocido(estado);
        }
    }

    @Test
    void esEstadoValido_conNombreValido_esTrue() {
        assertThat(service.esEstadoValido("PAGADO")).isTrue();
        assertThat(service.esEstadoValido("PENDIENTE")).isTrue();
        assertThat(service.esEstadoValido("EN_PROCESO")).isTrue();
        assertThat(service.esEstadoValido("RECHAZADO")).isTrue();
    }

    @Test
    void esEstadoValido_conNombreDesconocido_esFalse() {
        assertThat(service.esEstadoValido("APROBADO_PARCIALMENTE")).isFalse();
        assertThat(service.esEstadoValido("CANCELADO")).isFalse();
        assertThat(service.esEstadoValido("")).isFalse();
    }
}
