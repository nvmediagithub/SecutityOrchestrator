package org.example.infrastructure.services.integrated.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for API-BPMN contradictions found during analysis
 */
public class ApiBpmnContradiction {
    
    private UUID id;
    private String type;
    private String severity;
    private String title;
    private String description;
    private String apiEndpoint;
    private String bpmnElement;
    private String impact;
    private List<String> recommendations;
    private Map<String, Object> metadata;
    private String correlationId;
    
    public ApiBpmnContradiction() {
    }
    
    public ApiBpmnContradiction(String type, String severity, String title, String description) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.severity = severity;
        this.title = title;
        this.description = description;
    }
    
    public enum ContradictionType {
        MISSING_API_FOR_TASK,
        MISSING_TASK_FOR_API,
        METHOD_MISMATCH,
        PARAMETER_MISMATCH,
        RESPONSE_MISMATCH,
        SECURITY_MISMATCH,
        BUSINESS_LOGIC_MISMATCH,
        VALIDATION_MISMATCH
    }
    
    public enum Severity {
        CRITICAL, HIGH, MEDIUM, LOW
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getSeverity() {
        return severity;
    }
    
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getApiEndpoint() {
        return apiEndpoint;
    }
    
    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }
    
    public String getBpmnElement() {
        return bpmnElement;
    }
    
    public void setBpmnElement(String bpmnElement) {
        this.bpmnElement = bpmnElement;
    }
    
    public String getImpact() {
        return impact;
    }
    
    public void setImpact(String impact) {
        this.impact = impact;
    }
    
    public List<String> getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }
    
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}