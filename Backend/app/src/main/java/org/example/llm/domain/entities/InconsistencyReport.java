package org.example.llm.domain.entities;

import org.example.domain.valueobjects.InconsistencyType;
import org.example.domain.valueobjects.SeverityLevel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain entity representing a detected inconsistency between OpenAPI and BPMN
 */
public class InconsistencyReport {
    
    private final String id;
    private final InconsistencyType type;
    private final SeverityLevel severity;
    private final String title;
    private final String description;
    private final String openApiElement;
    private final String bpmnElement;
    private final String location;
    private final List<String> affectedComponents;
    private final List<String> recommendations;
    private final Map<String, Object> metadata;
    private final LocalDateTime detectedAt;
    private final ConfidenceLevel confidence;
    private final ImpactAssessment impact;
    private final boolean resolved;
    private final LocalDateTime resolvedAt;
    
    public InconsistencyReport(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.severity = builder.severity;
        this.title = builder.title;
        this.description = builder.description;
        this.openApiElement = builder.openApiElement;
        this.bpmnElement = builder.bpmnElement;
        this.location = builder.location;
        this.affectedComponents = builder.affectedComponents;
        this.recommendations = builder.recommendations;
        this.metadata = builder.metadata;
        this.detectedAt = builder.detectedAt;
        this.confidence = builder.confidence;
        this.impact = builder.impact;
        this.resolved = builder.resolved;
        this.resolvedAt = builder.resolvedAt;
    }
    
    // Getters
    public String getId() { return id; }
    public InconsistencyType getType() { return type; }
    public SeverityLevel getSeverity() { return severity; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getOpenApiElement() { return openApiElement; }
    public String getBpmnElement() { return bpmnElement; }
    public String getLocation() { return location; }
    public List<String> getAffectedComponents() { return affectedComponents; }
    public List<String> getRecommendations() { return recommendations; }
    public Map<String, Object> getMetadata() { return metadata; }
    public LocalDateTime getDetectedAt() { return detectedAt; }
    public ConfidenceLevel getConfidence() { return confidence; }
    public ImpactAssessment getImpact() { return impact; }
    public boolean isResolved() { return resolved; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    
    // Business methods
    public boolean isCritical() {
        return severity == SeverityLevel.CRITICAL;
    }
    
    public boolean isHighPriority() {
        return severity == SeverityLevel.HIGH || severity == SeverityLevel.CRITICAL;
    }
    
    public boolean hasHighConfidence() {
        return confidence == ConfidenceLevel.HIGH;
    }
    
    public String getSummary() {
        return String.format("[%s] %s: %s", severity.name(), type.name(), title);
    }
    
    // Builder pattern
    public static class Builder {
        private String id = UUID.randomUUID().toString();
        private InconsistencyType type;
        private SeverityLevel severity = SeverityLevel.MEDIUM;
        private String title = "";
        private String description = "";
        private String openApiElement = "";
        private String bpmnElement = "";
        private String location = "";
        private List<String> affectedComponents = List.of();
        private List<String> recommendations = List.of();
        private Map<String, Object> metadata = Map.of();
        private LocalDateTime detectedAt = LocalDateTime.now();
        private ConfidenceLevel confidence = ConfidenceLevel.MEDIUM;
        private ImpactAssessment impact = ImpactAssessment.MEDIUM;
        private boolean resolved = false;
        private LocalDateTime resolvedAt = null;
        
        public Builder id(String id) {
            this.id = id;
            return this;
        }
        
        public Builder type(InconsistencyType type) {
            this.type = type;
            return this;
        }
        
        public Builder severity(SeverityLevel severity) {
            this.severity = severity;
            return this;
        }
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder openApiElement(String openApiElement) {
            this.openApiElement = openApiElement;
            return this;
        }
        
        public Builder bpmnElement(String bpmnElement) {
            this.bpmnElement = bpmnElement;
            return this;
        }
        
        public Builder location(String location) {
            this.location = location;
            return this;
        }
        
        public Builder affectedComponents(List<String> affectedComponents) {
            this.affectedComponents = affectedComponents;
            return this;
        }
        
        public Builder recommendations(List<String> recommendations) {
            this.recommendations = recommendations;
            return this;
        }
        
        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }
        
        public Builder detectedAt(LocalDateTime detectedAt) {
            this.detectedAt = detectedAt;
            return this;
        }
        
        public Builder confidence(ConfidenceLevel confidence) {
            this.confidence = confidence;
            return this;
        }
        
        public Builder impact(ImpactAssessment impact) {
            this.impact = impact;
            return this;
        }
        
        public Builder resolved(boolean resolved) {
            this.resolved = resolved;
            return this;
        }
        
        public Builder resolvedAt(LocalDateTime resolvedAt) {
            this.resolvedAt = resolvedAt;
            return this;
        }
        
        public InconsistencyReport build() {
            if (type == null) {
                throw new IllegalArgumentException("Inconsistency type is required");
            }
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Title is required");
            }
            return new InconsistencyReport(this);
        }
    }
    
    // Enums and value classes
    public enum ConfidenceLevel {
        LOW(0.3),
        MEDIUM(0.7),
        HIGH(0.9);
        
        private final double score;
        
        ConfidenceLevel(double score) {
            this.score = score;
        }
        
        public double getScore() {
            return score;
        }
    }
    
    public enum ImpactAssessment {
        LOW("Low impact on system functionality"),
        MEDIUM("Medium impact requiring attention"),
        HIGH("High impact causing system issues"),
        CRITICAL("Critical impact blocking functionality");
        
        private final String description;
        
        ImpactAssessment(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
