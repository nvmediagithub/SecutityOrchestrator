package org.example.domain.entities.openapi;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Сущность для хранения результатов анализа эндпоинтов
 */
@Entity
@Table(name = "endpoint_analyses")
@EntityListeners(AuditingEntityListener.class)
public class EndpointAnalysis {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private OpenApiService service;
    
    @NotBlank
    @Column(name = "analysis_name", nullable = false)
    private String analysisName;
    
    private String description;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "analysis_type", nullable = false)
    private AnalysisType analysisType;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AnalysisStatus status = AnalysisStatus.PENDING;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    @Column(name = "total_endpoints")
    private Integer totalEndpoints = 0;
    
    @Column(name = "analyzed_endpoints")
    private Integer analyzedEndpoints = 0;
    
    @Column(name = "issues_found")
    private Integer issuesFound = 0;
    
    @Column(name = "security_issues")
    private Integer securityIssues = 0;
    
    @Column(name = "validation_issues")
    private Integer validationIssues = 0;
    
    @Column(name = "consistency_issues")
    private Integer consistencyIssues = 0;
    
    @Column(name = "performance_issues")
    private Integer performanceIssues = 0;
    
    @Column(name = "quality_score")
    private Double qualityScore;
    
    @Column(name = "analysis_config", columnDefinition = "CLOB")
    private String analysisConfig;
    
    @Column(name = "summary_report", columnDefinition = "CLOB")
    private String summaryReport;
    
    @Column(name = "detailed_report", columnDefinition = "CLOB")
    private String detailedReport;
    
    @ElementCollection
    @CollectionTable(name = "analysis_recommendations", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "recommendation", columnDefinition = "CLOB")
    private List<String> recommendations = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "analysis_tags", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Конструкторы
    public EndpointAnalysis() {}
    
    public EndpointAnalysis(String analysisName, AnalysisType analysisType) {
        this.analysisName = analysisName;
        this.analysisType = analysisType;
        this.status = AnalysisStatus.PENDING;
    }
    
    public EndpointAnalysis(String analysisName, String description, AnalysisType analysisType) {
        this(analysisName, analysisType);
        this.description = description;
    }
    
    // Геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public OpenApiService getService() { return service; }
    public void setService(OpenApiService service) { this.service = service; }
    
    public String getAnalysisName() { return analysisName; }
    public void setAnalysisName(String analysisName) { this.analysisName = analysisName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public AnalysisType getAnalysisType() { return analysisType; }
    public void setAnalysisType(AnalysisType analysisType) { this.analysisType = analysisType; }
    
    public AnalysisStatus getStatus() { return status; }
    public void setStatus(AnalysisStatus status) { this.status = status; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public Integer getTotalEndpoints() { return totalEndpoints; }
    public void setTotalEndpoints(Integer totalEndpoints) { this.totalEndpoints = totalEndpoints; }
    
    public Integer getAnalyzedEndpoints() { return analyzedEndpoints; }
    public void setAnalyzedEndpoints(Integer analyzedEndpoints) { this.analyzedEndpoints = analyzedEndpoints; }
    
    public Integer getIssuesFound() { return issuesFound; }
    public void setIssuesFound(Integer issuesFound) { this.issuesFound = issuesFound; }
    
    public Integer getSecurityIssues() { return securityIssues; }
    public void setSecurityIssues(Integer securityIssues) { this.securityIssues = securityIssues; }
    
    public Integer getValidationIssues() { return validationIssues; }
    public void setValidationIssues(Integer validationIssues) { this.validationIssues = validationIssues; }
    
    public Integer getConsistencyIssues() { return consistencyIssues; }
    public void setConsistencyIssues(Integer consistencyIssues) { this.consistencyIssues = consistencyIssues; }
    
    public Integer getPerformanceIssues() { return performanceIssues; }
    public void setPerformanceIssues(Integer performanceIssues) { this.performanceIssues = performanceIssues; }
    
    public Double getQualityScore() { return qualityScore; }
    public void setQualityScore(Double qualityScore) { this.qualityScore = qualityScore; }
    
    public String getAnalysisConfig() { return analysisConfig; }
    public void setAnalysisConfig(String analysisConfig) { this.analysisConfig = analysisConfig; }
    
    public String getSummaryReport() { return summaryReport; }
    public void setSummaryReport(String summaryReport) { this.summaryReport = summaryReport; }
    
    public String getDetailedReport() { return detailedReport; }
    public void setDetailedReport(String detailedReport) { this.detailedReport = detailedReport; }
    
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Вспомогательные методы
    public void startAnalysis() {
        this.status = AnalysisStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }
    
    public void completeAnalysis() {
        this.status = AnalysisStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
    
    public void failAnalysis(String errorMessage) {
        this.status = AnalysisStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
    }
    
    public void addRecommendation(String recommendation) {
        if (!recommendations.contains(recommendation)) {
            recommendations.add(recommendation);
        }
    }
    
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void incrementIssues(IssueType issueType) {
        this.issuesFound++;
        switch (issueType) {
            case SECURITY:
                this.securityIssues++;
                break;
            case VALIDATION:
                this.validationIssues++;
                break;
            case CONSISTENCY:
                this.consistencyIssues++;
                break;
            case PERFORMANCE:
                this.performanceIssues++;
                break;
        }
    }
    
    public boolean isCompleted() {
        return status == AnalysisStatus.COMPLETED;
    }
    
    public boolean isRunning() {
        return status == AnalysisStatus.RUNNING;
    }
    
    public boolean hasFailed() {
        return status == AnalysisStatus.FAILED;
    }
    
    public boolean isPending() {
        return status == AnalysisStatus.PENDING;
    }
    
    public double getProgressPercentage() {
        if (totalEndpoints == null || totalEndpoints == 0) {
            return 0.0;
        }
        return (analyzedEndpoints.doubleValue() / totalEndpoints.doubleValue()) * 100.0;
    }
    
    public int getTotalIssues() {
        return securityIssues + validationIssues + consistencyIssues + performanceIssues;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndpointAnalysis that = (EndpointAnalysis) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "EndpointAnalysis{" +
                "id=" + id +
                ", analysisName='" + analysisName + '\'' +
                ", analysisType=" + analysisType +
                ", status=" + status +
                ", totalEndpoints=" + totalEndpoints +
                ", issuesFound=" + issuesFound +
                '}';
    }
    
    // Перечисления
    public enum AnalysisType {
        QUICK_SCAN, FULL_ANALYSIS, SECURITY_AUDIT, VALIDATION_CHECK, CONSISTENCY_AUDIT, PERFORMANCE_ANALYSIS
    }
    
    public enum AnalysisStatus {
        PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
    }
    
    public enum IssueType {
        SECURITY, VALIDATION, CONSISTENCY, PERFORMANCE
    }
}