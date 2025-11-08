package org.example.domain.entities;

import org.example.domain.valueobjects.AnalysisStatus;
import org.example.domain.valueobjects.SpecificationId;
import org.example.domain.entities.openapi.OpenApiSpecification;

import jakarta.persistence.*;
import java.util.List;
import java.time.LocalDateTime;

/**
 * Сущность для хранения результатов анализа OpenAPI спецификаций
 */
@Entity
@Table(name = "openapi_analyses")
public class OpenApiAnalysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String analysisId;
    
    @Column(nullable = false)
    private String specificationId;
    
    private String specificationTitle;
    private String specificationVersion;
    
    @Enumerated(EnumType.STRING)
    private AnalysisStatus status;
    
    private Integer totalEndpoints;
    private Integer analyzedEndpoints;
    private Integer totalIssues;
    private Integer criticalIssues;
    private Integer highIssues;
    private Integer mediumIssues;
    private Integer lowIssues;
    private Integer securityIssues;
    private Integer validationIssues;
    private Integer inconsistencyIssues;
    
    private Long analysisDurationMs;
    private String analysisSummary;
    
    @ElementCollection
    private List<String> tags;
    
    @ElementCollection
    private List<String> endpointAnalyses;
    
    @ElementCollection
    private List<String> issues;
    
    @ElementCollection
    private List<String> securityChecks;
    
    @ElementCollection
    private List<String> inconsistencies;
    
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    
    private String errorMessage;
    private String analyzerVersion;
    
    private Boolean isActive = true;
    private Double progressPercentage;
    
    // Constructors
    public OpenApiAnalysis() {
        this.analysisId = "analysis_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
        this.createdAt = LocalDateTime.now();
        this.status = AnalysisStatus.PENDING;
        this.tags = new java.util.ArrayList<>();
        this.endpointAnalyses = new java.util.ArrayList<>();
        this.issues = new java.util.ArrayList<>();
        this.securityChecks = new java.util.ArrayList<>();
        this.inconsistencies = new java.util.ArrayList<>();
    }
    
    public OpenApiAnalysis(String specificationId, String specificationTitle) {
        this();
        this.specificationId = specificationId;
        this.specificationTitle = specificationTitle;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public String getSpecificationId() { return specificationId; }
    public void setSpecificationId(String specificationId) { this.specificationId = specificationId; }
    
    public String getSpecificationTitle() { return specificationTitle; }
    public void setSpecificationTitle(String specificationTitle) { this.specificationTitle = specificationTitle; }
    
    public String getSpecificationVersion() { return specificationVersion; }
    public void setSpecificationVersion(String specificationVersion) { this.specificationVersion = specificationVersion; }
    
    public AnalysisStatus getStatus() { return status; }
    public void setStatus(AnalysisStatus status) { this.status = status; }
    
    public Integer getTotalEndpoints() { return totalEndpoints; }
    public void setTotalEndpoints(Integer totalEndpoints) { this.totalEndpoints = totalEndpoints; }
    
    public Integer getAnalyzedEndpoints() { return analyzedEndpoints; }
    public void setAnalyzedEndpoints(Integer analyzedEndpoints) { this.analyzedEndpoints = analyzedEndpoints; }
    
    public Integer getTotalIssues() { return totalIssues; }
    public void setTotalIssues(Integer totalIssues) { this.totalIssues = totalIssues; }
    
    public Integer getCriticalIssues() { return criticalIssues; }
    public void setCriticalIssues(Integer criticalIssues) { this.criticalIssues = criticalIssues; }
    
    public Integer getHighIssues() { return highIssues; }
    public void setHighIssues(Integer highIssues) { this.highIssues = highIssues; }
    
    public Integer getMediumIssues() { return mediumIssues; }
    public void setMediumIssues(Integer mediumIssues) { this.mediumIssues = mediumIssues; }
    
    public Integer getLowIssues() { return lowIssues; }
    public void setLowIssues(Integer lowIssues) { this.lowIssues = lowIssues; }
    
    public Integer getSecurityIssues() { return securityIssues; }
    public void setSecurityIssues(Integer securityIssues) { this.securityIssues = securityIssues; }
    
    public Integer getValidationIssues() { return validationIssues; }
    public void setValidationIssues(Integer validationIssues) { this.validationIssues = validationIssues; }
    
    public Integer getInconsistencyIssues() { return inconsistencyIssues; }
    public void setInconsistencyIssues(Integer inconsistencyIssues) { this.inconsistencyIssues = inconsistencyIssues; }
    
    public Long getAnalysisDurationMs() { return analysisDurationMs; }
    public void setAnalysisDurationMs(Long analysisDurationMs) { this.analysisDurationMs = analysisDurationMs; }
    
    public String getAnalysisSummary() { return analysisSummary; }
    public void setAnalysisSummary(String analysisSummary) { this.analysisSummary = analysisSummary; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public List<String> getEndpointAnalyses() { return endpointAnalyses; }
    public void setEndpointAnalyses(List<String> endpointAnalyses) { this.endpointAnalyses = endpointAnalyses; }
    
    public List<String> getIssues() { return issues; }
    public void setIssues(List<String> issues) { this.issues = issues; }
    
    public List<String> getSecurityChecks() { return securityChecks; }
    public void setSecurityChecks(List<String> securityChecks) { this.securityChecks = securityChecks; }
    
    public List<String> getInconsistencies() { return inconsistencies; }
    public void setInconsistencies(List<String> inconsistencies) { this.inconsistencies = inconsistencies; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String getAnalyzerVersion() { return analyzerVersion; }
    public void setAnalyzerVersion(String analyzerVersion) { this.analyzerVersion = analyzerVersion; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Double getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Double progressPercentage) { this.progressPercentage = progressPercentage; }
    
    // Helper methods
    public boolean isCompleted() {
        return AnalysisStatus.COMPLETED.equals(status);
    }
    
    public boolean isInProgress() {
        return AnalysisStatus.IN_PROGRESS.equals(status) || AnalysisStatus.PENDING.equals(status);
    }
    
    public void startAnalysis() {
        this.status = AnalysisStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
        this.progressPercentage = 0.0;
    }
    
    public void completeAnalysis() {
        this.status = AnalysisStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.progressPercentage = 100.0;
        
        if (this.startedAt != null) {
            this.analysisDurationMs = java.time.Duration.between(this.startedAt, this.completedAt).toMillis();
        }
    }
    
    public void failAnalysis(String error) {
        this.status = AnalysisStatus.FAILED;
        this.completedAt = LocalDateTime.now();
        this.errorMessage = error;
        this.progressPercentage = 0.0;
    }
    
    public void updateProgress(double percentage) {
        this.progressPercentage = Math.min(100.0, Math.max(0.0, percentage));
    }
    
    @Override
    public String toString() {
        return "OpenApiAnalysis{" +
                "analysisId='" + analysisId + '\'' +
                ", specificationId='" + specificationId + '\'' +
                ", status=" + status +
                ", totalIssues=" + totalIssues +
                '}';
    }
}