package org.example.infrastructure.repositories;

import org.example.domain.entities.BpmnGateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for BpmnGateway entity
 */
@Repository
public interface BpmnGatewayRepository extends JpaRepository<BpmnGateway, UUID> {
    
    @Query("SELECT g FROM BpmnGateway g WHERE g.diagramId = :diagramId")
    List<BpmnGateway> findByDiagramId(@Param("diagramId") String diagramId);
    
    @Query("SELECT g FROM BpmnGateway g WHERE g.gatewayType = :gatewayType")
    List<BpmnGateway> findByGatewayType(@Param("gatewayType") String gatewayType);
    
    @Query("SELECT g FROM BpmnGateway g WHERE g.name = :name AND g.diagramId = :diagramId")
    Optional<BpmnGateway> findByNameAndDiagramId(@Param("name") String name, @Param("diagramId") String diagramId);
    
    @Query("SELECT g FROM BpmnGateway g WHERE g.direction = :direction")
    List<BpmnGateway> findByDirection(@Param("direction") String direction);
}