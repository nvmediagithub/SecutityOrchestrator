package org.example.infrastructure.services.integrated.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for partial analysis results (OpenAPI, BPMN, or Integrated)
 */
public class PartialAnalysisResult {
    
    private UUID id;
    private String analysisType;
    private String sourceId;
    private String sourceName;
    private AnalysisStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String analysisVersion;
    
    private List<String> findings;
    private List<String> recommendations;
    private List<String> warnings;
    private List<String> errors;
    
    private Map<String, Object> metrics;
    private Map<String, Object> metadata;
    
    private double qualityScore;
    private int totalIssues;
    private int criticalIssues;
    private int highIssues;
    private int mediumIssues;
    private int lowIssues;
    
    private String correlationId;
    private String errorMessage;
    
    public PartialAnalysisResult() {
    }
    
    public PartialAnalysisResult(String analysisType, String sourceId, String sourceName) {
        this.id = UUID.randomUUID();
        this.analysisType = analysisType;
        this.sourceId = sourceId;
        this.sourceName = sourceName;
        this.startTime = LocalDateTime.now();
        this.status = AnalysisStatus.PENDING;
    }
    
    public enum AnalysisStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED, CANCELLED
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getAnalysisType() {
        return analysisType;
    }
    
    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }
    
    public String getSourceId() {
        return sourceId;
    }
    
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    
    public String getSourceName() {
        return sourceName;
    }
    
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    
    public AnalysisStatus getStatus() {
        return status;
    }
    
    public void setStatus(AnalysisStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public String getAnalysisVersion() {
        return analysisVersion;
    }
    
    public void setAnalysisVersion(String analysisVersion) {
        this.analysisVersion = analysisVersion;
    }
    
    public List<String> getFindings() {
        return findings;
    }
    
    public void setFindings(List<String> findings) {
        this.findings = findings;
    }
    
    public List<String> getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
    
    public List<String> getWarnings() {
        return warnings;
    }
    
    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
    public Map<String, Object> getMetrics() {
        return metrics;
    }
    
    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public double getQualityScore() {
        return qualityScore;
    }
    
    public void setQualityScore(double qualityScore) {
        this.qualityScore = qualityScore;
    }
    
    public int getTotalIssues() {
        return totalIssues;
    }
    
    public void setTotalIssues(int totalIssues) {
        this.totalIssues = totalIssues;
    }
    
    public int getCriticalIssues() {
        return criticalIssues;
    }
    
    public void setCriticalIssues(int criticalIssues) {
        this.criticalIssues = criticalIssues;
    }
    
    public int getHighIssues() {
        return highIssues;
    }
    
    public void setHighIssues(int highIssues) {
        this.highIssues = highIssues;
    }
    
    public int getMediumIssues() {
        return mediumIssues;
    }
    
    public void setMediumIssues(int mediumIssues) {
        this.mediumIssues = mediumIssues;
    }
    
    public int getLowIssues() {
        return lowIssues;
    }
    
    public void setLowIssues(int lowIssues) {
        this.lowIssues = lowIssues;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }
    
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}