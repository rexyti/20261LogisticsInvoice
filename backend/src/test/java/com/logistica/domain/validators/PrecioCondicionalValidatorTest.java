package com.logistica.domain.validators;

import com.logistica.application.dtos.request.ContratoRequestDTO;
import com.logistica.domain.enums.TipoContrato;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PrecioCondicionalValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private ContratoRequestDTO dtoBase() {
        return ContratoRequestDTO.builder()
                .idContrato("CONT-001")
                .nombreConductor("Ana García")
                .tipoVehiculo("FURGONETA")
                .fechaInicio(LocalDate.of(2026, 1, 1))
                .fechaFinal(LocalDate.of(2026, 12, 31))
                .estadoSeguro("VIGENTE")
                .build();
    }

    @Test
    @DisplayName("T009 - POR_PARADA requiere precioParadas")
    void porParadaRequierePrecioParadas() {
        ContratoRequestDTO dto = dtoBase();
        dto.setTipoContrato(TipoContrato.POR_PARADA);
        dto.setPrecioParadas(null);

        Set<ConstraintViolation<ContratoRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("precioParadas")
                        && v.getMessage().contains("Por Parada")
        );
    }

    @Test
    @DisplayName("T009 - POR_PARADA es válido cuando precioParadas está presente")
    void porParadaValidoCuandoPrecioParadasPresente() {
        ContratoRequestDTO dto = dtoBase();
        dto.setTipoContrato(TipoContrato.POR_PARADA);
        dto.setPrecioParadas(new BigDecimal("20.00"));

        Set<ConstraintViolation<ContratoRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).noneMatch(v ->
                v.getPropertyPath().toString().equals("precioParadas")
        );
    }

    @Test
    @DisplayName("T009 - RECORRIDO_COMPLETO requiere precio")
    void recorridoCompletoRequierePrecio() {
        ContratoRequestDTO dto = dtoBase();
        dto.setTipoContrato(TipoContrato.RECORRIDO_COMPLETO);
        dto.setPrecio(null);

        Set<ConstraintViolation<ContratoRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("precio")
                        && v.getMessage().contains("Recorrido Completo")
        );
    }

    @Test
    @DisplayName("T009 - RECORRIDO_COMPLETO es válido cuando precio está presente")
    void recorridoCompletoValidoCuandoPrecioPresente() {
        ContratoRequestDTO dto = dtoBase();
        dto.setTipoContrato(TipoContrato.RECORRIDO_COMPLETO);
        dto.setPrecio(new BigDecimal("500.00"));

        Set<ConstraintViolation<ContratoRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).noneMatch(v ->
                v.getPropertyPath().toString().equals("precio")
        );
    }

    @Test
    @DisplayName("T009 - POR_PARADA ignora el campo precio")
    void porParadaIgnoraCampoPrecio() {
        ContratoRequestDTO dto = dtoBase();
        dto.setTipoContrato(TipoContrato.POR_PARADA);
        dto.setPrecioParadas(new BigDecimal("10.00"));
        dto.setPrecio(null);

        Set<ConstraintViolation<ContratoRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).noneMatch(v ->
                v.getPropertyPath().toString().equals("precio")
                        && v.getMessage().contains("Recorrido Completo")
        );
    }
}
