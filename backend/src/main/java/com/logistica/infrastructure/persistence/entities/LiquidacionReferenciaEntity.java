package com.logistica.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "liquidaciones_referencia")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionReferenciaEntity {

    @Id
    @Column(name = "id_liquidacion")
    private UUID idLiquidacion;
}
