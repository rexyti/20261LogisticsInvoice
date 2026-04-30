package com.logistica.RegistrarEstadoPago.unit;

import com.logistica.RegistrarEstadoPago.domain.enums.EstadoPagoEnum;
import com.logistica.RegistrarEstadoPago.domain.services.TransicionEstadoPagoService;
import com.logistica.RegistrarEstadoPago.exceptions.TransicionEstadoPagoInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransicionEstadoPagoServiceTest {

    private TransicionEstadoPagoService service;

    @BeforeEach
    void setUp() {
        service = new TransicionEstadoPagoService();
    }

    @Test
    void transicion_PENDIENTE_a_EN_PROCESO_esValida() {
        service.validarTransicion(EstadoPagoEnum.PENDIENTE, EstadoPagoEnum.EN_PROCESO);
    }

    @Test
    void transicion_PENDIENTE_a_PAGADO_esValida() {
        service.validarTransicion(EstadoPagoEnum.PENDIENTE, EstadoPagoEnum.PAGADO);
    }

    @Test
    void transicion_PENDIENTE_a_RECHAZADO_esValida() {
        service.validarTransicion(EstadoPagoEnum.PENDIENTE, EstadoPagoEnum.RECHAZADO);
    }

    @Test
    void transicion_EN_PROCESO_a_PAGADO_esValida() {
        service.validarTransicion(EstadoPagoEnum.EN_PROCESO, EstadoPagoEnum.PAGADO);
    }

    @Test
    void transicion_EN_PROCESO_a_RECHAZADO_esValida() {
        service.validarTransicion(EstadoPagoEnum.EN_PROCESO, EstadoPagoEnum.RECHAZADO);
    }

    @Test
    void transicion_PAGADO_a_EN_PROCESO_esInvalida() {
        assertThatThrownBy(() -> service.validarTransicion(EstadoPagoEnum.PAGADO, EstadoPagoEnum.EN_PROCESO))
                .isInstanceOf(TransicionEstadoPagoInvalidaException.class);
    }

    @Test
    void transicion_PAGADO_a_PENDIENTE_esInvalida() {
        assertThatThrownBy(() -> service.validarTransicion(EstadoPagoEnum.PAGADO, EstadoPagoEnum.PENDIENTE))
                .isInstanceOf(TransicionEstadoPagoInvalidaException.class);
    }

    @Test
    void transicion_PAGADO_a_RECHAZADO_esInvalida() {
        assertThatThrownBy(() -> service.validarTransicion(EstadoPagoEnum.PAGADO, EstadoPagoEnum.RECHAZADO))
                .isInstanceOf(TransicionEstadoPagoInvalidaException.class);
    }

    @Test
    void transicion_RECHAZADO_a_EN_PROCESO_esInvalida() {
        assertThatThrownBy(() -> service.validarTransicion(EstadoPagoEnum.RECHAZADO, EstadoPagoEnum.EN_PROCESO))
                .isInstanceOf(TransicionEstadoPagoInvalidaException.class);
    }

    @Test
    void transicion_RECHAZADO_a_PAGADO_esInvalida() {
        assertThatThrownBy(() -> service.validarTransicion(EstadoPagoEnum.RECHAZADO, EstadoPagoEnum.PAGADO))
                .isInstanceOf(TransicionEstadoPagoInvalidaException.class);
    }

    @Test
    void transicion_RECHAZADO_a_PENDIENTE_esInvalida() {
        assertThatThrownBy(() -> service.validarTransicion(EstadoPagoEnum.RECHAZADO, EstadoPagoEnum.PENDIENTE))
                .isInstanceOf(TransicionEstadoPagoInvalidaException.class);
    }

    @Test
    void esTransicionValida_retornaCorrectamente() {
        assertThat(service.esTransicionValida(EstadoPagoEnum.PENDIENTE, EstadoPagoEnum.EN_PROCESO)).isTrue();
        assertThat(service.esTransicionValida(EstadoPagoEnum.EN_PROCESO, EstadoPagoEnum.PAGADO)).isTrue();
        assertThat(service.esTransicionValida(EstadoPagoEnum.PAGADO, EstadoPagoEnum.EN_PROCESO)).isFalse();
        assertThat(service.esTransicionValida(EstadoPagoEnum.RECHAZADO, EstadoPagoEnum.PAGADO)).isFalse();
    }
}
