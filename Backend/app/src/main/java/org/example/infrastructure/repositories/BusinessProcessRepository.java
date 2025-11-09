package org.example.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.domain.entities.BusinessProcess;
import java.util.UUID;
import java.util.Optional;

/**
 * Repository for BusinessProcess entities
 */
@Repository
public interface BusinessProcessRepository extends JpaRepository<BusinessProcess, UUID> {
    Optional<BusinessProcess> findByName(String name);
    Optional<BusinessProcess> findByDiagramId(String diagramId);
}