package org.example.domain.dto.openapi;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for OpenAPI analysis response containing comprehensive analysis results
 */
public class OpenApiAnalysisResponse {
    
    private String analysisId;
    private String specificationId;
    private String specificationTitle;
    private String specificationVersion;
    private String status; // Using String instead of enum to avoid compile issues
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
    private List<String> tags;
    private List<String> endpointAnalyses; // Simplified: storing IDs instead of objects
    private List<String> issues; // Simplified: storing IDs instead of objects
    private List<String> securityChecks; // Simplified: storing IDs instead of objects
    private List<String> inconsistencies; // Simplified: storing IDs instead of objects
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String errorMessage;
    private String analyzerVersion;
    private Boolean isActive;
    private Double progressPercentage;
    
    // Constructors
    public OpenApiAnalysisResponse() {}
    
    // Static factory method for creating mock data
    public static OpenApiAnalysisResponse createMock(String analysisId, String specificationTitle) {
        OpenApiAnalysisResponse response = new OpenApiAnalysisResponse();
        response.setAnalysisId(analysisId);
        response.setSpecificationId("spec-" + System.currentTimeMillis());
        response.setSpecificationTitle(specificationTitle);
        response.setSpecificationVersion("1.0.0");
        response.setStatus("COMPLETED");
        response.setTotalEndpoints(5);
        response.setAnalyzedEndpoints(5);
        response.setTotalIssues(3);
        response.setCriticalIssues(1);
        response.setHighIssues(1);
        response.setLowIssues(1);
        response.setSecurityIssues(1);
        response.setValidationIssues(1);
        response.setInconsistencyIssues(1);
        response.setAnalysisDurationMs(1500L);
        response.setAnalysisSummary("Mock analysis completed successfully");
        response.setAnalyzerVersion("1.0.0");
        response.setProgressPercentage(100.0);
        response.setCreatedAt(LocalDateTime.now());
        response.setCompletedAt(LocalDateTime.now());
        response.setIsActive(false);
        return response;
    }
    
    // Static factory method for creating empty response
    public static OpenApiAnalysisResponse createEmpty() {
        return new OpenApiAnalysisResponse();
    }
    
    // Getters and Setters
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public String getSpecificationId() { return specificationId; }
    public void setSpecificationId(String specificationId) { this.specificationId = specificationId; }
    
    public String getSpecificationTitle() { return specificationTitle; }
    public void setSpecificationTitle(String specificationTitle) { this.specificationTitle = specificationTitle; }
    
    public String getSpecificationVersion() { return specificationVersion; }
    public void setSpecificationVersion(String specificationVersion) { this.specificationVersion = specificationVersion; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
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
        return "COMPLETED".equals(status);
    }
    
    public boolean isInProgress() {
        return "IN_PROGRESS".equals(status) || "PENDING".equals(status);
    }
    
    public boolean hasCriticalIssues() {
        return criticalIssues != null && criticalIssues > 0;
    }
    
    public boolean hasHighIssues() {
        return highIssues != null && highIssues > 0;
    }
    
    public String getStatusDescription() {
        return status != null ? "Status: " + status : null;
    }
    
    @Override
    public String toString() {
        return "OpenApiAnalysisResponse{" +
                "analysisId='" + analysisId + '\'' +
                ", specificationTitle='" + specificationTitle + '\'' +
                ", status='" + status + '\'' +
                ", totalEndpoints=" + totalEndpoints +
                ", totalIssues=" + totalIssues +
                ", progress=" + (progressPercentage != null ? String.format("%.1f%%", progressPercentage) : "N/A") +
                ", createdAt=" + createdAt +
                '}';
    }
}