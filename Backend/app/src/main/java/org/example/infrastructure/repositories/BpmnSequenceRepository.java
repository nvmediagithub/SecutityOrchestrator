package org.example.infrastructure.repositories;

import org.example.domain.entities.BpmnSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for BpmnSequence entity
 */
@Repository
public interface BpmnSequenceRepository extends JpaRepository<BpmnSequence, UUID> {
    
    @Query("SELECT s FROM BpmnSequence s WHERE s.diagramId = :diagramId")
    List<BpmnSequence> findByDiagramId(@Param("diagramId") String diagramId);
    
    @Query("SELECT s FROM BpmnSequence s WHERE s.sourceId = :sourceId")
    List<BpmnSequence> findBySourceId(@Param("sourceId") String sourceId);
    
    @Query("SELECT s FROM BpmnSequence s WHERE s.targetId = :targetId")
    List<BpmnSequence> findByTargetId(@Param("targetId") String targetId);
    
    @Query("SELECT s FROM BpmnSequence s WHERE s.isDefault = true AND s.gatewayId = :gatewayId")
    List<BpmnSequence> findDefaultSequenceByGatewayId(@Param("gatewayId") String gatewayId);
}