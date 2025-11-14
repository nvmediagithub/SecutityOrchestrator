package org.example.features.bpmn.infrastructure.repositories;

import lombok.RequiredArgsConstructor;
import org.example.features.bpmn.domain.entities.BpmnDiagram;
import org.example.features.bpmn.domain.repositories.BpmnDiagramRepository;
import org.example.shared.core.valueobjects.ModelId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA implementation of BPMN diagram repository
 */
@Repository
@RequiredArgsConstructor
public class JpaBpmnDiagramRepository implements BpmnDiagramRepository {

    private final SpringDataJpaBpmnDiagramRepository jpaRepository;

    @Override
    public BpmnDiagram save(BpmnDiagram diagram) {
        // Convert domain entity to JPA entity if needed
        BpmnDiagramEntity entity = convertToEntity(diagram);
        BpmnDiagramEntity saved = jpaRepository.save(entity);
        return convertToDomain(saved);
    }

    @Override
    public Optional<BpmnDiagram> findById(ModelId id) {
        return jpaRepository.findById(id.getValue().toString())
            .map(this::convertToDomain);
    }

    @Override
    public Optional<BpmnDiagram> findByDiagramId(String diagramId) {
        return jpaRepository.findByDiagramId(diagramId)
            .map(this::convertToDomain);
    }

    @Override
    public List<BpmnDiagram> findAllActive() {
        return jpaRepository.findByIsActiveTrue().stream()
            .map(this::convertToDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<BpmnDiagram> findByNameContaining(String name) {
        return jpaRepository.findByNameContainingIgnoreCase(name).stream()
            .map(this::convertToDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<BpmnDiagram> findAllOrderByCreatedAtDesc() {
        return jpaRepository.findAllByOrderByCreatedAtDesc().stream()
            .map(this::convertToDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(ModelId id) {
        String idStr = id.getValue().toString();
        if (jpaRepository.existsById(idStr)) {
            jpaRepository.deleteById(idStr);
            return true;
        }
        return false;
    }

    @Override
    public long countActive() {
        return jpaRepository.countActiveDiagrams();
    }

    // Conversion methods between domain and JPA entities
    private BpmnDiagramEntity convertToEntity(BpmnDiagram domain) {
        BpmnDiagramEntity entity = new BpmnDiagramEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue().toString());
        }
        entity.setDiagramName(domain.getDiagramName());
        entity.setDescription(domain.getDescription());
        entity.setBpmnContent(domain.getBpmnContent());
        entity.setVersion(domain.getVersion());
        entity.setDiagramType(domain.getDiagramType());
        entity.setTargetNamespace(domain.getTargetNamespace());
        entity.setIsActive(domain.isActive());
        entity.setExecutable(domain.isExecutable());
        entity.setProcessEngine(domain.getProcessEngine());
        entity.setDiagramId(domain.getDiagramId());
        entity.setProcessDefinitionId(domain.getProcessDefinitionId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setProcessSteps(domain.getProcessSteps());
        entity.setSequenceFlows(domain.getSequenceFlows());
        return entity;
    }

    private BpmnDiagram convertToDomain(BpmnDiagramEntity entity) {
        return BpmnDiagram.builder()
            .id(new ModelId(entity.getId()))
            .diagramName(entity.getDiagramName())
            .description(entity.getDescription())
            .bpmnContent(entity.getBpmnContent())
            .version(entity.getVersion())
            .diagramType(entity.getDiagramType())
            .targetNamespace(entity.getTargetNamespace())
            .isActive(entity.getIsActive())
            .executable(entity.getExecutable())
            .processEngine(entity.getProcessEngine())
            .diagramId(entity.getDiagramId())
            .processDefinitionId(entity.getProcessDefinitionId())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .processSteps(entity.getProcessSteps())
            .sequenceFlows(entity.getSequenceFlows())
            .build();
    }

    // Spring Data JPA interface
    public interface SpringDataJpaBpmnDiagramRepository extends JpaRepository<BpmnDiagramEntity, String> {
        Optional<BpmnDiagramEntity> findByDiagramId(String diagramId);
        List<BpmnDiagramEntity> findByIsActiveTrue();
        List<BpmnDiagramEntity> findByNameContainingIgnoreCase(String name);
        List<BpmnDiagramEntity> findByDescriptionContainingIgnoreCase(String description);
        List<BpmnDiagramEntity> findAllByOrderByCreatedAtDesc();
        long countActiveDiagrams();
    }
}