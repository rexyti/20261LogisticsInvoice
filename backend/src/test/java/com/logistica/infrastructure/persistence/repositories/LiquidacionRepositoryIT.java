package com.logistica.infrastructure.persistence.repositories;

import com.logistica.domain.liquidacion.enums.EstadoLiquidacion;
import com.logistica.domain.liquidacion.enums.TipoContratacion;
import com.logistica.infrastructure.contratos.persistence.entities.ContratoEntity;
import com.logistica.infrastructure.contratos.persistence.entities.TransportistaEntity;
import com.logistica.infrastructure.liquidacion.persistence.entities.LiquidacionEntity;
import com.logistica.infrastructure.liquidacion.persistence.repositories.LiquidacionJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("JpaRepository Integration Tests")
class LiquidacionRepositoryIT extends AbstractRepositoryIT {

    @Autowired
    private LiquidacionJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Debe guardar y encontrar una liquidación por su ID con relación OneToOne a Contrato")
    void shouldSaveAndFindByIdWithOneToOneContrato() {
        ContratoEntity contrato = createContrato();
        entityManager.persist(contrato);

        UUID rutaId = UUID.randomUUID();
        LiquidacionEntity entity = createLiquidacion(rutaId, contrato);

        LiquidacionEntity saved = repository.saveAndFlush(entity);
        entityManager.clear();

        Optional<LiquidacionEntity> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getIdRuta()).isEqualTo(rutaId);
        assertThat(found.get().getContrato()).isNotNull();
        assertThat(found.get().getContrato().getId()).isEqualTo(contrato.getId());
    }

    @Test
    @DisplayName("Debe fallar al intentar asociar el mismo contrato a dos liquidaciones (Restricción OneToOne)")
    void shouldFailWhenSameContratoIsUsedInTwoLiquidaciones() {
        ContratoEntity contrato = createContrato();
        entityManager.persist(contrato);

        LiquidacionEntity liq1 = createLiquidacion(UUID.randomUUID(), contrato);
        repository.saveAndFlush(liq1);

        LiquidacionEntity liq2 = createLiquidacion(UUID.randomUUID(), contrato);

        assertThrows(DataIntegrityViolationException.class, () -> {
            repository.saveAndFlush(liq2);
        }, "Debe lanzar excepción por violación de unicidad en id_contrato (OneToOne)");
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
        entity.setValorBase(new BigDecimal("100.0000"));
        entity.setValorFinal(new BigDecimal("100.0000"));
        entity.setFechaCalculo(OffsetDateTime.now());
        entity.setCreatedAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());
        return entity;
    }
}
