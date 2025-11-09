package org.example.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for storing context metrics
 */
@Entity
@Table(name = "context_metrics")
public class ContextMetrics {
    
    @Id
    private UUID id;
    
    @Column(name = "project_id", nullable = false)
    private String projectId;
    
    @Column(name = "context_type", nullable = false)
    private String contextType;
    
    @Column(name = "metric_name", nullable = false)
    private String metricName;
    
    @Column(name = "metric_value", columnDefinition = "DOUBLE")
    private Double metricValue;
    
    @Column(name = "unit", nullable = false)
    private String unit;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
    }
    
    public ContextMetrics() {
    }
    
    public ContextMetrics(String projectId, String contextType, String metricName, Double metricValue, String unit) {
        this.projectId = projectId;
        this.contextType = contextType;
        this.metricName = metricName;
        this.metricValue = metricValue;
        this.unit = unit;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public String getContextType() {
        return contextType;
    }
    
    public void setContextType(String contextType) {
        this.contextType = contextType;
    }
    
    public String getMetricName() {
        return metricName;
    }
    
    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }
    
    public Double getMetricValue() {
        return metricValue;
    }
    
    public void setMetricValue(Double metricValue) {
        this.metricValue = metricValue;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}