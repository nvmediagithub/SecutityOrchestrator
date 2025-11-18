package org.example.features.bpmn.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataJpaBpmnDiagramRepository extends JpaRepository<BpmnDiagramEntity, String> {

    Optional<BpmnDiagramEntity> findByDiagramId(String diagramId);

    List<BpmnDiagramEntity> findByIsActiveTrue();

    List<BpmnDiagramEntity> findByDiagramNameContainingIgnoreCase(String diagramName);

    List<BpmnDiagramEntity> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COUNT(d) FROM BpmnDiagramEntity d WHERE d.isActive = true")
    long countActiveDiagrams();
}