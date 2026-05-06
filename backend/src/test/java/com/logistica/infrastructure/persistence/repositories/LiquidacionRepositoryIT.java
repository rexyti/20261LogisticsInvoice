package com.logistica.infrastructure.persistence.repositories;

import com.logistica.domain.liquidacion.enums.EstadoLiquidacion;
import com.logistica.domain.liquidacion.enums.TipoContratacion;
import com.logistica.infrastructure.liquidacion.persistence.entities.ContratoEntity;
import com.logistica.infrastructure.liquidacion.persistence.entities.Entity;
import com.logistica.infrastructure.liquidacion.persistence.repositories.JpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("JpaRepository Integration Tests")
class LiquidacionRepositoryIT extends AbstractRepositoryIT {

    @Autowired
    private JpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Debe guardar y encontrar una liquidación por su ID con relación OneToOne a Contrato")
    void shouldSaveAndFindByIdWithOneToOneContrato() {
        // Given: Un contrato persistido
        ContratoEntity contrato = createContrato();
        entityManager.persist(contrato);

        // Given: Una entidad liquidación válida
        UUID rutaId = UUID.randomUUID();
        Entity entity = createLiquidacion(rutaId, contrato);

        // When
        Entity saved = repository.saveAndFlush(entity);
        entityManager.clear(); // Limpiar cache para forzar lectura de DB
        
        Optional<Entity> found = repository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getIdRuta()).isEqualTo(rutaId);
        assertThat(found.get().getContrato()).isNotNull();
        assertThat(found.get().getContrato().getId()).isEqualTo(contrato.getId());
    }

    @Test
    @DisplayName("Debe fallar al intentar asociar el mismo contrato a dos liquidaciones (Restricción OneToOne)")
    void shouldFailWhenSameContratoIsUsedInTwoLiquidaciones() {
        // Given: Un contrato y una liquidación ya persistida
        ContratoEntity contrato = createContrato();
        entityManager.persist(contrato);
        
        Entity liq1 = createLiquidacion(UUID.randomUUID(), contrato);
        repository.saveAndFlush(liq1);

        // When & Then: Intentar guardar otra liquidación con el MISMO contrato
        Entity liq2 = createLiquidacion(UUID.randomUUID(), contrato);
        
        assertThrows(DataIntegrityViolationException.class, () -> {
            repository.saveAndFlush(liq2);
        }, "Debe lanzar excepción por violación de unicidad en id_contrato (OneToOne)");
    }

    // --- Helpers de Creación (Clean Code) ---

    private ContratoEntity createContrato() {
        ContratoEntity contrato = new ContratoEntity();
        contrato.setId(UUID.randomUUID());
        contrato.setTipoContratacion(TipoContratacion.POR_PARADA);
        contrato.setTarifa(new BigDecimal("10.0000"));
        return contrato;
    }

    private Entity createLiquidacion(UUID rutaId, ContratoEntity contrato) {
        Entity entity = new Entity();
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
