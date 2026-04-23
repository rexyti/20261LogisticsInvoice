package com.logistica.domain.validators;

import com.logistica.application.dtos.request.ContratoRequestDTO;
import com.logistica.domain.enums.TipoContrato;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PrecioCondicionalValidator implements ConstraintValidator<ValidPrecioCondicional, ContratoRequestDTO> {

    @Override
    public boolean isValid(ContratoRequestDTO dto, ConstraintValidatorContext context) {
        if (dto.getTipoContrato() == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        if (dto.getTipoContrato() == TipoContrato.POR_PARADA) {
            if (dto.getPrecioParadas() == null) {
                context.buildConstraintViolationWithTemplate(
                                "El precio por parada es obligatorio para contratos de tipo Por Parada")
                        .addPropertyNode("precioParadas")
                        .addConstraintViolation();
                return false;
            }
            return true;
        }

        if (dto.getTipoContrato() == TipoContrato.RECORRIDO_COMPLETO) {
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
