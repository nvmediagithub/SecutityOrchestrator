package org.example.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for storing context analysis metrics and results
 */
@Entity
@Table(name = "context_metrics")
public class ContextMetrics {
    
    @Id
    private UUID id;
    
    @Column(name = "analysis_id", nullable = false)
    private String analysisId;
    
    @Column(name = "context_type", nullable = false)
    private String contextType;
    
    @Column(name = "metrics_json", columnDefinition = "TEXT")
    private String metricsJson;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public ContextMetrics() {
    }
    
    public ContextMetrics(String analysisId, String contextType, String metricsJson) {
        this.analysisId = analysisId;
        this.contextType = contextType;
        this.metricsJson = metricsJson;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getAnalysisId() {
        return analysisId;
    }
    
    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
    }
    
    public String getContextType() {
        return contextType;
    }
    
    public void setContextType(String contextType) {
        this.contextType = contextType;
    }
    
    public String getMetricsJson() {
        return metricsJson;
    }
    
    public void setMetricsJson(String metricsJson) {
        this.metricsJson = metricsJson;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}