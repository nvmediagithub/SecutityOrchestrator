package org.example.infrastructure.services.integrated.dto;

import org.example.domain.valueobjects.SeverityLevel;

import java.time.LocalDateTime;

/**
 * DTO для приоритизированных проблем
 */
public class PrioritizedIssue {
    
    private String id;
    private String title;
    private String description;
    private String category; // "api", "bpmn", "integration", "security", "performance"
    private SeverityLevel priority;
    private double businessImpactScore;
    private double riskScore;
    private String status; // "open", "in_progress", "resolved", "dismissed"
    
    // Связанные элементы
    private String relatedElementId; // ID API endpoint или BPMN task
    private String relatedElementType; // "endpoint", "task", "flow", "data_object"
    
    // Метаданные
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private String assignedTo;
    private String resolution;
    
    // Конструкторы
    public PrioritizedIssue() {}
    
    public PrioritizedIssue(String id, String title, String description, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
    }
    
    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public SeverityLevel getPriority() { return priority; }
    public void setPriority(SeverityLevel priority) { this.priority = priority; }
    
    public double getBusinessImpactScore() { return businessImpactScore; }
    public void setBusinessImpactScore(double businessImpactScore) { this.businessImpactScore = businessImpactScore; }
    
    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double riskScore) { this.riskScore = riskScore; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getRelatedElementId() { return relatedElementId; }
    public void setRelatedElementId(String relatedElementId) { this.relatedElementId = relatedElementId; }
    
    public String getRelatedElementType() { return relatedElementType; }
    public void setRelatedElementType(String relatedElementType) { this.relatedElementType = relatedElementType; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
    
    // Вспомогательные методы
    public boolean isHighImpact() {
        return businessImpactScore >= 0.7;
    }
    
    public boolean isHighRisk() {
        return riskScore >= 0.7;
    }
    
    public boolean isResolved() {
        return "resolved".equals(status);
    }
    
    public boolean isOpen() {
        return "open".equals(status);
    }
    
    public void markAsResolved(String resolution) {
        this.status = "resolved";
        this.resolution = resolution;
        this.resolvedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "PrioritizedIssue{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", priority=" + priority +
                ", businessImpactScore=" + businessImpactScore +
                '}';
    }
}