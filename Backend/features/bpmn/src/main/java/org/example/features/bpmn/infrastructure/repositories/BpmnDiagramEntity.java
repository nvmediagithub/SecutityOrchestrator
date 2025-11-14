package org.example.features.bpmn.infrastructure.repositories;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import jakarta.persistence.Table;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA entity for BPMN diagrams (legacy entity reused for now)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bpmn_diagrams")
// @EntityListeners(AuditingEntityListener.class)
public class BpmnDiagramEntity {

    @Id
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "diagram_name")
    private String diagramName;

    @Column(length = 1000)
    private String description;

    @Column(name = "bpmn_content", columnDefinition = "TEXT")
    private String bpmnContent;

    private String version;

    @Column(name = "diagram_type")
    private String diagramType;

    @Column(name = "target_namespace")
    private String targetNamespace;

    @Column(name = "is_active")
    private Boolean isActive;

    private Boolean executable;

    @Column(name = "process_engine")
    private String processEngine;

    @Column(name = "diagram_id")
    private String diagramId;

    @Column(name = "process_definition_id")
    private String processDefinitionId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ElementCollection
    @CollectionTable(name = "bpmn_diagram_process_steps", joinColumns = @JoinColumn(name = "diagram_id"))
    @Column(name = "process_step")
    private List<String> processSteps;

    @ElementCollection
    @CollectionTable(name = "bpmn_diagram_sequence_flows", joinColumns = @JoinColumn(name = "diagram_id"))
    @Column(name = "sequence_flow")
    private List<String> sequenceFlows;
}