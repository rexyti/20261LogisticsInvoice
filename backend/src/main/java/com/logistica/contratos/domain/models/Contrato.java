package com.logistica.contratos.domain.models;

import com.logistica.contratos.domain.enums.TipoVehiculo;
import com.logistica.contratos.domain.exceptions.ContratoInvalidoException;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class Contrato {
    private UUID id;
    private String idContrato;
    private String tipoContrato;
    private Transportista transportista;
    private TipoVehiculo tipoVehiculo;
    private Boolean esPorParada;
    private BigDecimal precioParadas;
    private BigDecimal precio;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinal;
    private Seguro seguro;

    public static Contrato crear(
            String idContrato,
            String tipoContrato,
            Transportista transportista,
            TipoVehiculo tipoVehiculo,
            Boolean esPorParada,
            BigDecimal precioParadas,
            BigDecimal precio,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFinal,
            Seguro seguro) {

        if (idContrato == null || idContrato.isBlank())
            throw new ContratoInvalidoException("idContrato es obligatorio");

        if (transportista == null)
            throw new ContratoInvalidoException("El transportista es obligatorio");

        if (tipoVehiculo == null)
            throw new ContratoInvalidoException("El tipo de vehículo es obligatorio");

        if (esPorParada == null)
            throw new ContratoInvalidoException("esPorParada es obligatorio");

        if (fechaInicio == null || fechaFinal == null)
            throw new ContratoInvalidoException("Las fechas son obligatorias");

        if (!fechaFinal.isAfter(fechaInicio))
            throw new ContratoInvalidoException(
                    "fechaFinal debe ser estrictamente mayor a fechaInicio");

        if (Boolean.TRUE.equals(esPorParada) && precioParadas == null)
            throw new ContratoInvalidoException(
                    "precioParadas es obligatorio cuando el contrato es por parada");

        if (Boolean.FALSE.equals(esPorParada) && precio == null)
            throw new ContratoInvalidoException(
                    "precio es obligatorio cuando el contrato es recorrido completo");

        return Contrato.builder()
                .id(UUID.randomUUID())
                .idContrato(idContrato)
                .tipoContrato(tipoContrato)
                .transportista(transportista)
                .tipoVehiculo(tipoVehiculo)
                .esPorParada(esPorParada)
                .precioParadas(Boolean.TRUE.equals(esPorParada) ? precioParadas : null)
                .precio(Boolean.FALSE.equals(esPorParada) ? precio : null)
                .fechaInicio(fechaInicio)
                .fechaFinal(fechaFinal)
                .seguro(seguro)
                .build();
    }
}