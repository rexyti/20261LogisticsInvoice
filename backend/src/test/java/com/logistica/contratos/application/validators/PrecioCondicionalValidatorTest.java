package com.logistica.contratos.application.validators;

import com.logistica.contratos.application.dtos.request.ContratoRequestDTO;
import com.logistica.contratos.application.dtos.request.SeguroRequestDTO;
import com.logistica.contratos.domain.enums.ContratosTipoVehiculo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PrecioCondicionalValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private ContratoRequestDTO dtoBase() {
        SeguroRequestDTO seguro = new SeguroRequestDTO();
        seguro.setNumeroPoliza("POL-001");
        seguro.setEstado("VIGENTE");

        ContratoRequestDTO dto = new ContratoRequestDTO();
        dto.setIdContrato("CONT-001");
        dto.setTipoContrato("MENSAJERIA");
        dto.setTransportistaId(UUID.randomUUID());
        dto.setTipoVehiculo(ContratosTipoVehiculo.VAN);
        dto.setFechaInicio(LocalDateTime.of(2026, 1, 1, 0, 0));
        dto.setFechaFinal(LocalDateTime.of(2026, 12, 31, 0, 0));
        dto.setSeguro(seguro);
        return dto;
    }

    @Test
    @DisplayName("T009 - esPorParada=true requiere precioParadas")
    void porParadaRequierePrecioParadas() {
        ContratoRequestDTO dto = dtoBase();
        dto.setEsPorParada(true);
        dto.setPrecioParadas(null);

        Set<ConstraintViolation<ContratoRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("precioParadas")
                        && v.getMessage().contains("Por Parada")
        );
    }

    @Test
    @DisplayName("T009 - esPorParada=true es válido cuando precioParadas está presente")
    void porParadaValidoCuandoPrecioParadasPresente() {
        ContratoRequestDTO dto = dtoBase();
        dto.setEsPorParada(true);
        dto.setPrecioParadas(new BigDecimal("20.00"));

        Set<ConstraintViolation<ContratoRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).noneMatch(v ->
                v.getPropertyPath().toString().equals("precioParadas")
        );
    }

    @Test
    @DisplayName("T009 - esPorParada=false requiere precio")
    void recorridoCompletoRequierePrecio() {
        ContratoRequestDTO dto = dtoBase();
        dto.setEsPorParada(false);
        dto.setPrecio(null);

        Set<ConstraintViolation<ContratoRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("precio")
                        && v.getMessage().contains("Recorrido Completo")
        );
    }

    @Test
    @DisplayName("T009 - esPorParada=false es válido cuando precio está presente")
    void recorridoCompletoValidoCuandoPrecioPresente() {
        ContratoRequestDTO dto = dtoBase();
        dto.setEsPorParada(false);
        dto.setPrecio(new BigDecimal("500.00"));

        Set<ConstraintViolation<ContratoRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).noneMatch(v ->
                v.getPropertyPath().toString().equals("precio")
        );
    }

    @Test
    @DisplayName("T009 - esPorParada=true ignora el campo precio")
    void porParadaIgnoraCampoPrecio() {
        ContratoRequestDTO dto = dtoBase();
        dto.setEsPorParada(true);
        dto.setPrecioParadas(new BigDecimal("10.00"));
        dto.setPrecio(null);

        Set<ConstraintViolation<ContratoRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).noneMatch(v ->
                v.getPropertyPath().toString().equals("precio")
                        && v.getMessage().contains("Recorrido Completo")
        );
    }
}
