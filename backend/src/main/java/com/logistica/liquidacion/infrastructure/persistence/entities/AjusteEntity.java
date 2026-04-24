package com.logistica.liquidacion.infrastructure.persistence.entities;

import com.logistica.liquidacion.domain.enums.TipoAjuste;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "ajustes")
@Getter
@Setter
public class AjusteEntity extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_liquidacion", nullable = false)
    private LiquidacionEntity liquidacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoAjuste tipo;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal monto;

    @Column(nullable = false)
    private String motivo;
    

}
