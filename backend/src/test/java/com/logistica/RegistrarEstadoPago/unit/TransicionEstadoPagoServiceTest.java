package com.logistica.RegistrarEstadoPago.unit;

import com.logistica.domain.registrarEstadoPago.enums.RegistrarEstadoPagoEstadoPagoEnum;
import com.logistica.domain.registrarEstadoPago.services.TransicionEstadoPagoService;
import com.logistica.domain.registrarEstadoPago.exceptions.TransicionEstadoPagoInvalidaException;
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
        service.validarTransicion(RegistrarEstadoPagoEstadoPagoEnum.PENDIENTE, RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO);
    }

    @Test
    void transicion_PENDIENTE_a_PAGADO_esValida() {
        service.validarTransicion(RegistrarEstadoPagoEstadoPagoEnum.PENDIENTE, RegistrarEstadoPagoEstadoPagoEnum.PAGADO);
    }

    @Test
    void transicion_PENDIENTE_a_RECHAZADO_esValida() {
        service.validarTransicion(RegistrarEstadoPagoEstadoPagoEnum.PENDIENTE, RegistrarEstadoPagoEstadoPagoEnum.RECHAZADO);
    }

    @Test
    void transicion_EN_PROCESO_a_PAGADO_esValida() {
        service.validarTransicion(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO, RegistrarEstadoPagoEstadoPagoEnum.PAGADO);
    }

    @Test
    void transicion_EN_PROCESO_a_RECHAZADO_esValida() {
        service.validarTransicion(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO, RegistrarEstadoPagoEstadoPagoEnum.RECHAZADO);
    }

    @Test
    void transicion_PAGADO_a_EN_PROCESO_esInvalida() {
        assertThatThrownBy(() -> service.validarTransicion(RegistrarEstadoPagoEstadoPagoEnum.PAGADO, RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO))
                .isInstanceOf(TransicionEstadoPagoInvalidaException.class);
    }

    @Test
    void transicion_PAGADO_a_PENDIENTE_esInvalida() {
        assertThatThrownBy(() -> service.validarTransicion(RegistrarEstadoPagoEstadoPagoEnum.PAGADO, RegistrarEstadoPagoEstadoPagoEnum.PENDIENTE))
                .isInstanceOf(TransicionEstadoPagoInvalidaException.class);
    }

    @Test
    void transicion_PAGADO_a_RECHAZADO_esInvalida() {
        assertThatThrownBy(() -> service.validarTransicion(RegistrarEstadoPagoEstadoPagoEnum.PAGADO, RegistrarEstadoPagoEstadoPagoEnum.RECHAZADO))
                .isInstanceOf(TransicionEstadoPagoInvalidaException.class);
    }

    @Test
    void transicion_RECHAZADO_a_EN_PROCESO_esInvalida() {
        assertThatThrownBy(() -> service.validarTransicion(RegistrarEstadoPagoEstadoPagoEnum.RECHAZADO, RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO))
                .isInstanceOf(TransicionEstadoPagoInvalidaException.class);
    }

    @Test
    void transicion_RECHAZADO_a_PAGADO_esInvalida() {
        assertThatThrownBy(() -> service.validarTransicion(RegistrarEstadoPagoEstadoPagoEnum.RECHAZADO, RegistrarEstadoPagoEstadoPagoEnum.PAGADO))
                .isInstanceOf(TransicionEstadoPagoInvalidaException.class);
    }

    @Test
    void transicion_RECHAZADO_a_PENDIENTE_esInvalida() {
        assertThatThrownBy(() -> service.validarTransicion(RegistrarEstadoPagoEstadoPagoEnum.RECHAZADO, RegistrarEstadoPagoEstadoPagoEnum.PENDIENTE))
                .isInstanceOf(TransicionEstadoPagoInvalidaException.class);
    }

    @Test
    void esTransicionValida_retornaCorrectamente() {
        assertThat(service.esTransicionValida(RegistrarEstadoPagoEstadoPagoEnum.PENDIENTE, RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO)).isTrue();
        assertThat(service.esTransicionValida(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO, RegistrarEstadoPagoEstadoPagoEnum.PAGADO)).isTrue();
        assertThat(service.esTransicionValida(RegistrarEstadoPagoEstadoPagoEnum.PAGADO, RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO)).isFalse();
        assertThat(service.esTransicionValida(RegistrarEstadoPagoEstadoPagoEnum.RECHAZADO, RegistrarEstadoPagoEstadoPagoEnum.PAGADO)).isFalse();
    }
}
