package com.logistica.infrastructure.persistence.repositories;

import com.logistica.infrastructure.persistence.entities.SeguroEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeguroJpaRepository extends JpaRepository<SeguroEntity, Long> {
}
