package org.example.domain.entities.llm;

import org.example.domain.valueobjects.SeverityLevel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain entity representing an LLM-generated suggestion for improving consistency
 */
public class LLMSuggestion {
    
    private final String id;
    private final String title;
    private final String description;
    private final String detailedExplanation;
    private final SuggestionCategory category;
    private final SuggestionPriority priority;
    private final SeverityLevel severityLevel;
    private final List<String> stepsToImplement;
    private final List<String> benefits;
    private final List<String> potentialRisks;
    private final List<String> relatedStandards;
    private final String targetComponent;
    private final Map<String, Object> metadata;
    private final LocalDateTime createdAt;
    private final ConfidenceLevel confidence;
    private final ImplementationEffort effort;
    private final BusinessImpact businessImpact;
    private final boolean implemented;
    private final LocalDateTime implementedAt;
    private final String implementationNotes;
    
    public LLMSuggestion(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.detailedExplanation = builder.detailedExplanation;
        this.category = builder.category;
        this.priority = builder.priority;
        this.severityLevel = builder.severityLevel;
        this.stepsToImplement = builder.stepsToImplement;
        this.benefits = builder.benefits;
        this.potentialRisks = builder.potentialRisks;
        this.relatedStandards = builder.relatedStandards;
        this.targetComponent = builder.targetComponent;
        this.metadata = builder.metadata;
        this.createdAt = builder.createdAt;
        this.confidence = builder.confidence;
        this.effort = builder.effort;
        this.businessImpact = builder.businessImpact;
        this.implemented = builder.implemented;
        this.implementedAt = builder.implementedAt;
        this.implementationNotes = builder.implementationNotes;
    }
    
    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDetailedExplanation() { return detailedExplanation; }
    public SuggestionCategory getCategory() { return category; }
    public SuggestionPriority getPriority() { return priority; }
    public SeverityLevel getSeverityLevel() { return severityLevel; }
    public List<String> getStepsToImplement() { return stepsToImplement; }
    public List<String> getBenefits() { return benefits; }
    public List<String> getPotentialRisks() { return potentialRisks; }
    public List<String> getRelatedStandards() { return relatedStandards; }
    public String getTargetComponent() { return targetComponent; }
    public Map<String, Object> getMetadata() { return metadata; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public ConfidenceLevel getConfidence() { return confidence; }
    public ImplementationEffort getEffort() { return effort; }
    public BusinessImpact getBusinessImpact() { return businessImpact; }
    public boolean isImplemented() { return implemented; }
    public LocalDateTime getImplementedAt() { return implementedAt; }
    public String getImplementationNotes() { return implementationNotes; }
    
    // Business methods
    public boolean isHighPriority() {
        return priority == SuggestionPriority.HIGH || priority == SuggestionPriority.CRITICAL;
    }
    
    public boolean isLowEffort() {
        return effort == ImplementationEffort.SMALL || effort == ImplementationEffort.MEDIUM;
    }
    
    public boolean hasHighConfidence() {
        return confidence == ConfidenceLevel.HIGH;
    }
    
    public boolean isQuickWin() {
        return isLowEffort() && isHighPriority() && hasHighConfidence();
    }
    
    public String getSummary() {
        return String.format("[%s] %s: %s", priority.name(), category.name(), title);
    }
    
    public String getRiskBenefitRatio() {
        if (benefits.size() == 0) return "No benefits identified";
        if (potentialRisks.size() == 0) return "No risks identified";
        return String.format("Benefits: %d, Risks: %d", benefits.size(), potentialRisks.size());
    }
    
    // Builder pattern
    public static class Builder {
        private String id = UUID.randomUUID().toString();
        private String title = "";
        private String description = "";
        private String detailedExplanation = "";
        private SuggestionCategory category = SuggestionCategory.QUALITY_IMPROVEMENT;
        private SuggestionPriority priority = SuggestionPriority.MEDIUM;
        private SeverityLevel severityLevel = SeverityLevel.LOW;
        private List<String> stepsToImplement = List.of();
        private List<String> benefits = List.of();
        private List<String> potentialRisks = List.of();
        private List<String> relatedStandards = List.of();
        private String targetComponent = "";
        private Map<String, Object> metadata = Map.of();
        private LocalDateTime createdAt = LocalDateTime.now();
        private ConfidenceLevel confidence = ConfidenceLevel.MEDIUM;
        private ImplementationEffort effort = ImplementationEffort.MEDIUM;
        private BusinessImpact businessImpact = BusinessImpact.MEDIUM;
        private boolean implemented = false;
        private LocalDateTime implementedAt = null;
        private String implementationNotes = "";
        
        public Builder id(String id) {
            this.id = id;
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
        
        public Builder detailedExplanation(String detailedExplanation) {
            this.detailedExplanation = detailedExplanation;
            return this;
        }
        
        public Builder category(SuggestionCategory category) {
            this.category = category;
            return this;
        }
        
        public Builder priority(SuggestionPriority priority) {
            this.priority = priority;
            return this;
        }
        
        public Builder severityLevel(SeverityLevel severityLevel) {
            this.severityLevel = severityLevel;
            return this;
        }
        
        public Builder stepsToImplement(List<String> stepsToImplement) {
            this.stepsToImplement = stepsToImplement;
            return this;
        }
        
        public Builder benefits(List<String> benefits) {
            this.benefits = benefits;
            return this;
        }
        
        public Builder potentialRisks(List<String> potentialRisks) {
            this.potentialRisks = potentialRisks;
            return this;
        }
        
        public Builder relatedStandards(List<String> relatedStandards) {
            this.relatedStandards = relatedStandards;
            return this;
        }
        
        public Builder targetComponent(String targetComponent) {
            this.targetComponent = targetComponent;
            return this;
        }
        
        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Builder confidence(ConfidenceLevel confidence) {
            this.confidence = confidence;
            return this;
        }
        
        public Builder effort(ImplementationEffort effort) {
            this.effort = effort;
            return this;
        }
        
        public Builder businessImpact(BusinessImpact businessImpact) {
            this.businessImpact = businessImpact;
            return this;
        }
        
        public Builder implemented(boolean implemented) {
            this.implemented = implemented;
            return this;
        }
        
        public Builder implementedAt(LocalDateTime implementedAt) {
            this.implementedAt = implementedAt;
            return this;
        }
        
        public Builder implementationNotes(String implementationNotes) {
            this.implementationNotes = implementationNotes;
            return this;
        }
        
        public LLMSuggestion build() {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Title is required");
            }
            if (description == null || description.trim().isEmpty()) {
                throw new IllegalArgumentException("Description is required");
            }
            return new LLMSuggestion(this);
        }
    }
    
    // Enums and value classes
    public enum SuggestionCategory {
        API_DESIGN("API Design improvements"),
        BPMN_OPTIMIZATION("BPMN Process optimization"),
        CONSISTENCY_IMPROVEMENT("Consistency improvements"),
        SECURITY_ENHANCEMENT("Security enhancements"),
        DOCUMENTATION("Documentation improvements"),
        VALIDATION_RULES("Validation rule enhancements"),
        ERROR_HANDLING("Error handling improvements"),
        PERFORMANCE("Performance optimizations"),
        QUALITY_IMPROVEMENT("General quality improvements"),
        COMPLIANCE("Compliance-related improvements");
        
        private final String description;
        
        SuggestionCategory(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public enum SuggestionPriority {
        LOW("Low priority - nice to have"),
        MEDIUM("Medium priority - should be addressed"),
        HIGH("High priority - important for system integrity"),
        CRITICAL("Critical priority - must be addressed immediately");
        
        private final String description;
        
        SuggestionPriority(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
        
        public int getScore() {
            switch (this) {
                case LOW: return 1;
                case MEDIUM: return 2;
                case HIGH: return 3;
                case CRITICAL: return 4;
                default: return 2;
            }
        }
    }
    
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
    
    public enum ImplementationEffort {
        SMALL("Small effort - can be implemented quickly"),
        MEDIUM("Medium effort - requires moderate resources"),
        LARGE("Large effort - requires significant resources"),
        VERY_LARGE("Very large effort - major undertaking");
        
        private final String description;
        
        ImplementationEffort(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public enum BusinessImpact {
        LOW("Low business impact"),
        MEDIUM("Medium business impact"),
        HIGH("High business impact"),
        CRITICAL("Critical business impact");
        
        private final String description;
        
        BusinessImpact(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}