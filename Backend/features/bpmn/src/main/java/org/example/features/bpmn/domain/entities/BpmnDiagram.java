package org.example.features.bpmn.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.shared.core.valueobjects.ModelId;

import java.time.LocalDateTime;
import java.util.List;

/**
 * BPMN diagram entity representing a business process model
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmnDiagram {
    private ModelId id;
    private String diagramName;
    private String description;
    private String bpmnContent;
    private String version;
    private String diagramType;
    private String targetNamespace;
    private boolean isActive;
    private boolean executable;
    private String processEngine;
    private String diagramId;
    private String processDefinitionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> processSteps;
    private List<String> sequenceFlows;

    /**
     * Validates the BPMN diagram content
     */
    public boolean isValid() {
        return bpmnContent != null && !bpmnContent.trim().isEmpty()
            && diagramName != null && !diagramName.trim().isEmpty();
    }

    /**
     * Gets the primary process ID from the diagram
     */
    public String getPrimaryProcessId() {
        if (processDefinitionId != null) {
            return processDefinitionId;
        }
        return diagramId;
    }
}