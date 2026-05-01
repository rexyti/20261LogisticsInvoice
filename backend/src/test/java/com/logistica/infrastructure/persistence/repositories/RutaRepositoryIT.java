package com.logistica.infrastructure.persistence.repositories;

import com.logistica.cierreRuta.domain.enums.EstadoParada;
import com.logistica.cierreRuta.domain.enums.EstadoProcesamiento;
import com.logistica.cierreRuta.infrastructure.persistence.entities.CierreRutaParadaEntity;
import com.logistica.cierreRuta.infrastructure.persistence.entities.CierreRutaRutaEntity;
import com.logistica.cierreRuta.infrastructure.persistence.entities.CierreRutaTransportistaEntity;
import com.logistica.cierreRuta.infrastructure.persistence.repositories.CierreRutaRutaJpaRepository;
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
    private CierreRutaRutaJpaRepository repository;

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

        CierreRutaRutaEntity ruta = CierreRutaRutaEntity.builder()
                .rutaId(UUID.randomUUID())
                .transportista(transportista)
                .fechaInicioTransito(LocalDateTime.now())
                .fechaCierre(LocalDateTime.now().plusHours(4))
                .estadoProcesamiento(EstadoProcesamiento.OK)
                .build();

        CierreRutaParadaEntity parada = CierreRutaParadaEntity.builder()
                .paradaId(UUID.randomUUID())
                .paqueteId(UUID.randomUUID())
                .estado(EstadoParada.EXITOSA)
                .build();
        
        ruta.addParada(parada);

        // When
        CierreRutaRutaEntity saved = repository.save(ruta);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<CierreRutaRutaEntity> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTransportista().getNombre()).isEqualTo("Juan Transport");
        assertThat(found.get().getParadas()).hasSize(1);
        assertThat(found.get().getParadas().get(0).getPaqueteId()).isNotNull();
    }
}
