package com.logistica.liquidacion.infrastructure.persistence.entities;

import com.logistica.liquidacion.domain.enums.TipoContratacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "contratos")
@Getter
@Setter
public class ContratoEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contratacion", nullable = false)
    private TipoContratacion tipoContratacion;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal tarifa;
}
