package org.example.infrastructure.repositories;

import org.example.domain.entities.BpmnDiagram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for BpmnDiagram entities
 * Provides CRUD operations and custom search methods
 */
@Repository
public interface BpmnDiagramRepository extends JpaRepository<BpmnDiagram, Long> {
    
    /**
     * Find by diagram ID
     */
    Optional<BpmnDiagram> findByDiagramId(String diagramId);
    
    /**
     * Find all active diagrams
     */
    List<BpmnDiagram> findByIsActiveTrue();
    
    /**
     * Find by name containing (case-insensitive)
     */
    List<BpmnDiagram> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find by description keyword
     */
    List<BpmnDiagram> findByDescriptionContainingIgnoreCase(String description);
    
    /**
     * Find by process definition ID
     */
    Optional<BpmnDiagram> findByProcessDefinitionId(String processDefinitionId);
    
    /**
     * Find by version
     */
    List<BpmnDiagram> findByVersion(String version);
    
    /**
     * Find by diagram type
     */
    List<BpmnDiagram> findByDiagramType(String diagramType);
    
    /**
     * Find executable diagrams
     */
    List<BpmnDiagram> findByExecutableTrue();
    
    /**
     * Find non-executable diagrams
     */
    List<BpmnDiagram> findByExecutableFalse();
    
    /**
     * Find by process engine
     */
    List<BpmnDiagram> findByProcessEngine(String processEngine);
    
    /**
     * Find by target namespace
     */
    List<BpmnDiagram> findByTargetNamespace(String targetNamespace);
    
    /**
     * Count active diagrams
     */
    @Query("SELECT COUNT(b) FROM BpmnDiagram b WHERE b.isActive = true")
    long countActiveDiagrams();
    
    /**
     * Find diagrams ordered by creation date
     */
    List<BpmnDiagram> findAllByOrderByCreatedAtDesc();
}