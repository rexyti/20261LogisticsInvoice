package com.logistica.infrastructure.persistence.repositories;

import com.logistica.liquidacion.domain.enums.EstadoLiquidacion;
import com.logistica.liquidacion.domain.enums.TipoAjuste;
import com.logistica.liquidacion.domain.enums.TipoContratacion;
import com.logistica.liquidacion.infrastructure.persistence.entities.LiquidacionAjusteEntity;
import com.logistica.liquidacion.infrastructure.persistence.entities.LiquidacionContratoEntity;
import com.logistica.liquidacion.infrastructure.persistence.entities.LiquidacionEntity;
import com.logistica.liquidacion.infrastructure.persistence.repositories.LiquidacionAjusteJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AjusteJpaRepository Integration Tests")
class AjusteRepositoryIT extends AbstractRepositoryIT {

    @Autowired
    private LiquidacionAjusteJpaRepository ajusteRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Debe encontrar ajustes por el ID de la liquidación")
    void shouldFindByLiquidacionId() {
        // Given: Un contrato y una liquidación persistida
        LiquidacionContratoEntity contrato = createContrato();
        entityManager.persist(contrato);
        
        LiquidacionEntity liq = createLiquidacion(UUID.randomUUID(), contrato);
        entityManager.persist(liq);

        // Given: Dos ajustes asociados
        LiquidacionAjusteEntity a1 = createAjuste(liq, TipoAjuste.BONO, "10.0000", "Bono 1");
        LiquidacionAjusteEntity a2 = createAjuste(liq, TipoAjuste.PENALIZACION, "5.0000", "Descuento 1");
        
        ajusteRepository.saveAllAndFlush(List.of(a1, a2));
        entityManager.clear(); // Forzar carga de DB

        // When
        List<LiquidacionAjusteEntity> results = ajusteRepository.findByLiquidacion_Id(liq.getId());

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).extracting(LiquidacionAjusteEntity::getMotivo)
                .containsExactlyInAnyOrder("Bono 1", "Descuento 1");
    }

    // --- Helpers de Creación ---

    private LiquidacionContratoEntity createContrato() {
        LiquidacionContratoEntity contrato = new LiquidacionContratoEntity();
        contrato.setId(UUID.randomUUID());
        contrato.setTipoContratacion(TipoContratacion.POR_PARADA);
        contrato.setTarifa(new BigDecimal("10.0000"));
        return contrato;
    }

    private LiquidacionEntity createLiquidacion(UUID rutaId, LiquidacionContratoEntity contrato) {
        LiquidacionEntity entity = new LiquidacionEntity();
        entity.setId(UUID.randomUUID());
        entity.setIdRuta(rutaId);
        entity.setContrato(contrato);
        entity.setEstado(EstadoLiquidacion.CALCULADA);
        entity.setValorBase(BigDecimal.TEN);
        entity.setValorFinal(BigDecimal.TEN);
        entity.setFechaCalculo(OffsetDateTime.now());
        entity.setCreatedAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());
        return entity;
    }

    private LiquidacionAjusteEntity createAjuste(LiquidacionEntity liq, TipoAjuste tipo, String monto, String motivo) {
        LiquidacionAjusteEntity ajuste = new LiquidacionAjusteEntity();
        ajuste.setId(UUID.randomUUID());
        ajuste.setLiquidacion(liq);
        ajuste.setTipo(tipo);
        ajuste.setMonto(new BigDecimal(monto));
        ajuste.setMotivo(motivo);
        ajuste.setCreatedAt(OffsetDateTime.now());
        ajuste.setUpdatedAt(OffsetDateTime.now());
        return ajuste;
    }
}
