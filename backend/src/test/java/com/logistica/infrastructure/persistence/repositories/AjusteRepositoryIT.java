package com.logistica.infrastructure.persistence.repositories;

import com.logistica.domain.liquidacion.enums.EstadoLiquidacion;
import com.logistica.domain.liquidacion.enums.TipoAjuste;
import com.logistica.domain.liquidacion.enums.TipoContratacion;
import com.logistica.infrastructure.contratos.persistence.entities.ContratoEntity;
import com.logistica.infrastructure.contratos.persistence.entities.TransportistaEntity;
import com.logistica.infrastructure.liquidacion.persistence.entities.AjusteEntity;
import com.logistica.infrastructure.liquidacion.persistence.entities.LiquidacionEntity;
import com.logistica.infrastructure.liquidacion.persistence.repositories.AjusteJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AjusteJpaRepository Integration Tests")
class AjusteRepositoryIT extends AbstractRepositoryIT {

    @Autowired
    private AjusteJpaRepository ajusteRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Debe encontrar ajustes por el ID de la liquidación")
    void shouldFindByLiquidacionId() {
        ContratoEntity contrato = createContrato();
        entityManager.persist(contrato);

        LiquidacionEntity liq = createLiquidacion(UUID.randomUUID(), contrato);
        entityManager.persist(liq);

        AjusteEntity a1 = createAjuste(liq, TipoAjuste.BONO, "10.0000", "Bono 1");
        AjusteEntity a2 = createAjuste(liq, TipoAjuste.PENALIZACION, "5.0000", "Descuento 1");

        ajusteRepository.saveAllAndFlush(List.of(a1, a2));
        entityManager.clear();

        List<AjusteEntity> results = ajusteRepository.findByLiquidacion_Id(liq.getId());

        assertThat(results).hasSize(2);
        assertThat(results).extracting(AjusteEntity::getMotivo)
                .containsExactlyInAnyOrder("Bono 1", "Descuento 1");
    }

    private ContratoEntity createContrato() {
        TransportistaEntity transportista = TransportistaEntity.builder()
                .nombre("Test Transportista")
                .build();
        entityManager.persist(transportista);

        ContratoEntity contrato = new ContratoEntity();
        contrato.setIdContrato("CONT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        contrato.setTipoContrato("MENSAJERIA");
        contrato.setEsPorParada(false);
        contrato.setTipoVehiculo("VAN");
        contrato.setFechaInicio(LocalDateTime.now());
        contrato.setFechaFinal(LocalDateTime.now().plusMonths(1));
        contrato.setTransportista(transportista);
        contrato.setTipoContratacion(TipoContratacion.POR_PARADA.name());
        contrato.setTarifa(new BigDecimal("10.0000"));
        return contrato;
    }

    private LiquidacionEntity createLiquidacion(UUID rutaId, ContratoEntity contrato) {
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

    private AjusteEntity createAjuste(LiquidacionEntity liq, TipoAjuste tipo, String monto, String motivo) {
        AjusteEntity ajuste = new AjusteEntity();
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
