package org.example.infrastructure.repositories;

import org.example.domain.entities.BpmnEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for BpmnEvent entity
 */
@Repository
public interface BpmnEventRepository extends JpaRepository<BpmnEvent, UUID> {
    
    @Query("SELECT e FROM BpmnEvent e WHERE e.diagramId = :diagramId")
    List<BpmnEvent> findByDiagramId(@Param("diagramId") String diagramId);
    
    @Query("SELECT e FROM BpmnEvent e WHERE e.eventType = :eventType")
    List<BpmnEvent> findByEventType(@Param("eventType") String eventType);
    
    @Query("SELECT e FROM BpmnEvent e WHERE e.name = :name AND e.diagramId = :diagramId")
    Optional<BpmnEvent> findByNameAndDiagramId(@Param("name") String name, @Param("diagramId") String diagramId);
    
    @Query("SELECT e FROM BpmnEvent e WHERE e.isStartEvent = true")
    List<BpmnEvent> findStartEvents();
    
    @Query("SELECT e FROM BpmnEvent e WHERE e.isEndEvent = true")
    List<BpmnEvent> findEndEvents();
}