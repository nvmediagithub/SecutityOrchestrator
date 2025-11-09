package org.example.infrastructure.services.integrated.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для интегрированных рекомендаций
 */
public class IntegratedRecommendation {
    
    private String id;
    private String type; // "CONTRADICTION_RESOLUTION", "PROCESS_IMPROVEMENT", "API_ENHANCEMENT", "SECURITY_HARDENING"
    private String title;
    private String description;
    private String priority; // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    
    // Оценка усилий и влияния
    private String effortEstimate; // "1-2 days", "1-2 weeks", "3-4 weeks", "1-2 months"
    private double businessImpact;
    private double technicalComplexity;
    private double costEstimate;
    
    // Связанные элементы
    private String relatedIssueId;
    private List<String> relatedElementIds;
    private String relatedElementType; // "api", "bpmn", "integration"
    
    // Статус выполнения
    private String status; // "proposed", "approved", "in_progress", "completed", "rejected"
    private String assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dueDate;
    private LocalDateTime completedAt;
    
    // Детали реализации
    private String implementationSteps;
    private List<String> requiredChanges;
    private List<String> dependencies;
    private String expectedOutcome;
    
    // Метаданные
    private String category;
    private List<String> tags;
    
    // Конструкторы
    public IntegratedRecommendation() {}
    
    public IntegratedRecommendation(String id, String title, String description, String type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
    }
    
    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public String getEffortEstimate() { return effortEstimate; }
    public void setEffortEstimate(String effortEstimate) { this.effortEstimate = effortEstimate; }
    
    public double getBusinessImpact() { return businessImpact; }
    public void setBusinessImpact(double businessImpact) { this.businessImpact = businessImpact; }
    
    public double getTechnicalComplexity() { return technicalComplexity; }
    public void setTechnicalComplexity(double technicalComplexity) { this.technicalComplexity = technicalComplexity; }
    
    public double getCostEstimate() { return costEstimate; }
    public void setCostEstimate(double costEstimate) { this.costEstimate = costEstimate; }
    
    public String getRelatedIssueId() { return relatedIssueId; }
    public void setRelatedIssueId(String relatedIssueId) { this.relatedIssueId = relatedIssueId; }
    
    public List<String> getRelatedElementIds() { return relatedElementIds; }
    public void setRelatedElementIds(List<String> relatedElementIds) { this.relatedElementIds = relatedElementIds; }
    
    public String getRelatedElementType() { return relatedElementType; }
    public void setRelatedElementType(String relatedElementType) { this.relatedElementType = relatedElementType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public String getImplementationSteps() { return implementationSteps; }
    public void setImplementationSteps(String implementationSteps) { this.implementationSteps = implementationSteps; }
    
    public List<String> getRequiredChanges() { return requiredChanges; }
    public void setRequiredChanges(List<String> requiredChanges) { this.requiredChanges = requiredChanges; }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    
    public String getExpectedOutcome() { return expectedOutcome; }
    public void setExpectedOutcome(String expectedOutcome) { this.expectedOutcome = expectedOutcome; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    // Вспомогательные методы
    public boolean isHighPriority() {
        return "HIGH".equals(priority) || "CRITICAL".equals(priority);
    }
    
    public boolean isHighImpact() {
        return businessImpact >= 0.7;
    }
    
    public boolean isComplex() {
        return technicalComplexity >= 0.7;
    }
    
    public boolean isInProgress() {
        return "in_progress".equals(status);
    }
    
    public boolean isCompleted() {
        return "completed".equals(status);
    }
    
    public boolean isOverdue() {
        return dueDate != null && LocalDateTime.now().isAfter(dueDate) && !isCompleted();
    }
    
    public void markAsCompleted() {
        this.status = "completed";
        this.completedAt = LocalDateTime.now();
    }
    
    public void markAsInProgress() {
        this.status = "in_progress";
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addTag(String tag) {
        if (tags == null) {
            tags = new java.util.ArrayList<>();
        }
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void addRequiredChange(String change) {
        if (requiredChanges == null) {
            requiredChanges = new java.util.ArrayList<>();
        }
        if (!requiredChanges.contains(change)) {
            requiredChanges.add(change);
        }
    }
    
    @Override
    public String toString() {
        return "IntegratedRecommendation{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}