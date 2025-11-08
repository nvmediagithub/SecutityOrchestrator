package org.example.domain.entities;

import org.example.domain.valueobjects.SeverityLevel;
import org.example.domain.valueobjects.SpecificationId;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность для хранения проблем, найденных в OpenAPI спецификациях
 */
@Entity
@Table(name = "api_issues")
public class ApiIssue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String issueId;
    
    @Column(nullable = false)
    private String specificationId;
    
    private String specificationTitle;
    
    @Enumerated(EnumType.STRING)
    private SeverityLevel severity;
    
    private String category; // security, validation, consistency, documentation
    private String title;
    private String description;
    private String location; // path, method, parameter, schema
    
    @Column(columnDefinition = "TEXT")
    private String recommendation;
    
    private Double confidence; // 0.0 - 1.0
    
    private String status; // OPEN, RESOLVED, IGNORED, FALSE_POSITIVE
    
    @ElementCollection
    private java.util.List<String> affectedElements;
    
    private String analysisId;
    private String ruleId; // Идентификатор правила, которое сгенерировало проблему
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    
    private String resolution;
    private String resolvedBy;
    
    private Boolean isActive = true;
    private String[] tags;
    
    // Constructors
    public ApiIssue() {
        this.issueId = "issue_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "OPEN";
        this.severity = SeverityLevel.MEDIUM;
        this.category = "general";
        this.affectedElements = new java.util.ArrayList<>();
        this.isActive = true;
    }
    
    public ApiIssue(String specificationId, String title, String description, SeverityLevel severity) {
        this();
        this.specificationId = specificationId;
        this.title = title;
        this.description = description;
        this.severity = severity;
    }
    
    // Helper methods
    public void markAsResolved(String resolution, String resolvedBy) {
        this.status = "RESOLVED";
        this.resolvedAt = LocalDateTime.now();
        this.resolution = resolution;
        this.resolvedBy = resolvedBy;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsIgnored() {
        this.status = "IGNORED";
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsFalsePositive() {
        this.status = "FALSE_POSITIVE";
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isResolved() {
        return "RESOLVED".equals(status);
    }
    
    public boolean isOpen() {
        return "OPEN".equals(status);
    }
    
    public boolean isCritical() {
        return SeverityLevel.CRITICAL.equals(severity);
    }
    
    public boolean isHigh() {
        return SeverityLevel.HIGH.equals(severity);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getIssueId() { return issueId; }
    public void setIssueId(String issueId) { this.issueId = issueId; }
    
    public String getSpecificationId() { return specificationId; }
    public void setSpecificationId(String specificationId) { this.specificationId = specificationId; }
    
    public String getSpecificationTitle() { return specificationTitle; }
    public void setSpecificationTitle(String specificationTitle) { this.specificationTitle = specificationTitle; }
    
    public SeverityLevel getSeverity() { return severity; }
    public void setSeverity(SeverityLevel severity) { this.severity = severity; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { 
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public java.util.List<String> getAffectedElements() { return affectedElements; }
    public void setAffectedElements(java.util.List<String> affectedElements) { this.affectedElements = affectedElements; }
    
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
    
    public String getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public String[] getTags() { return tags; }
    public void setTags(String[] tags) { this.tags = tags; }
    
    @Override
    public String toString() {
        return "ApiIssue{" +
                "issueId='" + issueId + '\'' +
                ", specificationId='" + specificationId + '\'' +
                ", severity=" + severity +
                ", category='" + category + '\'' +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiIssue apiIssue = (ApiIssue) o;
        return issueId != null ? issueId.equals(apiIssue.issueId) : apiIssue.issueId == null;
    }
    
    @Override
    public int hashCode() {
        return issueId != null ? issueId.hashCode() : 0;
    }
}