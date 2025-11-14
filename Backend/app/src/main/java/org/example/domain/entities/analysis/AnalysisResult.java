package org.example.domain.entities.analysis;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Базовый класс для всех результатов анализа в системе SecurityOrchestrator
 */
public abstract class AnalysisResult {
    protected String specId;
    protected String analysisType;
    protected Map<String, Object> findings;
    protected Map<String, Object> recommendations;

    // Constructors and other methods remain the same

    // Setters for the new fields
    public void setId(String id) {
        // This is a workaround since id is final, but we'll handle it in subclasses
    }

    public void setSpecId(String specId) {
        this.specId = specId;
    }

    public void setStatus(String status) {
        // Status is final, handle in subclasses
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        // CreatedAt is final, handle in subclasses
    }

    public void setFindings(Map<String, Object> findings) {
        this.findings = findings;
    }

    public void setRecommendations(Map<String, Object> recommendations) {
        this.recommendations = recommendations;
    }

    // Getters for the new fields
    public String getSpecId() {
        return specId;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public Map<String, Object> getFindings() {
        return findings;
    }

    public Map<String, Object> getRecommendations() {
        return recommendations;
    }
    protected final UUID id;
    protected final LocalDateTime createdAt;
    protected final LocalDateTime updatedAt;
    protected final String status;
    protected final String errorMessage;
    protected final Double confidenceScore;
    
    protected AnalysisResult(Builder<?> builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = builder.status;
        this.errorMessage = builder.errorMessage;
        this.confidenceScore = builder.confidenceScore;
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public Double getConfidenceScore() {
        return confidenceScore;
    }
    
    public boolean isSuccessful() {
        return "SUCCESS".equals(status);
    }
    
    public boolean hasError() {
        return errorMessage != null && !errorMessage.trim().isEmpty();
    }
    
    // Builder pattern для наследников
    public abstract static class Builder<T extends Builder<T>> {
        private UUID id;
        private LocalDateTime createdAt;
        private String status = "PENDING";
        private String errorMessage;
        private Double confidenceScore;
        
        public T id(UUID id) {
            this.id = id;
            return self();
        }
        
        public T createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return self();
        }
        
        public T status(String status) {
            this.status = status;
            return self();
        }
        
        public T errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return self();
        }
        
        public T confidenceScore(Double confidenceScore) {
            this.confidenceScore = confidenceScore;
            return self();
        }
        
        protected abstract T self();
        
        public abstract AnalysisResult build();
    }
}