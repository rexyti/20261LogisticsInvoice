package com.logistica.contratos.application.validators;

import com.logistica.contratos.application.dtos.request.ContratoRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PrecioCondicionalValidator implements ConstraintValidator<ValidPrecioCondicional, ContratoRequestDTO> {

    @Override
    public boolean isValid(ContratoRequestDTO dto, ConstraintValidatorContext context) {
        if (dto.getEsPorParada() == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        if (Boolean.TRUE.equals(dto.getEsPorParada())) {
            if (dto.getPrecioParadas() == null) {
                context.buildConstraintViolationWithTemplate(
                                "El precio por parada es obligatorio para contratos de tipo Por Parada")
                        .addPropertyNode("precioParadas")
                        .addConstraintViolation();
                return false;
            }
            return true;
        }

        if (Boolean.FALSE.equals(dto.getEsPorParada())) {
            if (dto.getPrecio() == null) {
                context.buildConstraintViolationWithTemplate(
                                "El precio es obligatorio para contratos de tipo Recorrido Completo")
                        .addPropertyNode("precio")
                        .addConstraintViolation();
                return false;
            }
            return true;
        }

        return true;
    }
}
