package com.logistica.infrastructure.persistence.repositories;

import com.logistica.cierreRuta.domain.enums.EstadoParada;
import com.logistica.cierreRuta.domain.enums.EstadoProcesamiento;
import com.logistica.cierreRuta.infrastructure.persistence.entities.ParadaEntity;
import com.logistica.cierreRuta.infrastructure.persistence.entities.RutaEntity;
import com.logistica.cierreRuta.infrastructure.persistence.entities.CierreRutaTransportistaEntity;
import com.logistica.cierreRuta.infrastructure.persistence.repositories.RutaJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RutaJpaRepository Integration Tests")
class RutaRepositoryIT extends AbstractRepositoryIT {

    @Autowired
    private RutaJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Debe persistir una ruta completa con su transportista y paradas")
    void shouldPersistFullRuta() {
        // Given
        CierreRutaTransportistaEntity transportista = CierreRutaTransportistaEntity.builder()
                .conductorId(UUID.randomUUID())
                .nombre("Juan Transport")
                .build();
        entityManager.persist(transportista);

        RutaEntity ruta = RutaEntity.builder()
                .rutaId(UUID.randomUUID())
                .transportista(transportista)
                .fechaInicioTransito(LocalDateTime.now())
                .fechaCierre(LocalDateTime.now().plusHours(4))
                .estadoProcesamiento(EstadoProcesamiento.OK)
                .build();

        ParadaEntity parada = ParadaEntity.builder()
                .paradaId(UUID.randomUUID())
                .paqueteId(UUID.randomUUID())
                .estado(EstadoParada.EXITOSA)
                .build();
        
        ruta.addParada(parada);

        // When
        RutaEntity saved = repository.save(ruta);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<RutaEntity> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTransportista().getNombre()).isEqualTo("Juan Transport");
        assertThat(found.get().getParadas()).hasSize(1);
        assertThat(found.get().getParadas().get(0).getPaqueteId()).isNotNull();
    }
}
