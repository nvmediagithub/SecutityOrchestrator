package org.example.infrastructure.services.integrated.dto;

import org.example.domain.valueobjects.SeverityLevel;
import org.example.infrastructure.services.integrated.analysis.ApiBpmnContradictionAnalyzer;
import org.example.infrastructure.services.integrated.dto.ComprehensiveAnalysisRequest.BpmnDiagramRequest;
import org.example.infrastructure.services.llm.OpenApiAnalysisService;
import org.example.infrastructure.services.bpmn.BpmnAnalysisService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO для результатов комплексного анализа
 * Содержит результаты анализа OpenAPI, BPMN и интегрированного анализа
 */
public class ComprehensiveAnalysisResult {
    
    private String analysisId;
    private String projectId;
    private ComprehensiveAnalysisRequest request;
    private LocalDateTime createdAt;
    private String status; // "IN_PROGRESS", "COMPLETED", "FAILED"
    
    // Результаты анализа
    private PartialAnalysisResult openApiAnalysis;
    private PartialAnalysisResult bpmnAnalysis;
    private PartialAnalysisResult integratedAnalysis;
    
    // Агрегированные метрики
    private int totalIssues;
    private int criticalIssues;
    private int highIssues;
    private int mediumIssues;
    private int lowIssues;
    
    private double overallScore;
    private double securityScore;
    private double processScore;
    private double integrationScore;
    
    // Сводная информация
    private List<String> summary;
    private List<String> keyFindings;
    private List<String> riskFactors;
    
    // Метаданные
    private Map<String, Object> metadata;
    private long processingTimeMs;
    private String analyzerVersion;
    
    // Конструкторы
    public ComprehensiveAnalysisResult() {}
    
    // Геттеры и сеттеры
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }
    
    public ComprehensiveAnalysisRequest getRequest() { return request; }
    public void setRequest(ComprehensiveAnalysisRequest request) { this.request = request; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public PartialAnalysisResult getOpenApiAnalysis() { return openApiAnalysis; }
    public void setOpenApiAnalysis(PartialAnalysisResult openApiAnalysis) { this.openApiAnalysis = openApiAnalysis; }
    
    public PartialAnalysisResult getBpmnAnalysis() { return bpmnAnalysis; }
    public void setBpmnAnalysis(PartialAnalysisResult bpmnAnalysis) { this.bpmnAnalysis = bpmnAnalysis; }
    
    public PartialAnalysisResult getIntegratedAnalysis() { return integratedAnalysis; }
    public void setIntegratedAnalysis(PartialAnalysisResult integratedAnalysis) { this.integratedAnalysis = integratedAnalysis; }
    
    public int getTotalIssues() { return totalIssues; }
    public void setTotalIssues(int totalIssues) { this.totalIssues = totalIssues; }
    
    public int getCriticalIssues() { return criticalIssues; }
    public void setCriticalIssues(int criticalIssues) { this.criticalIssues = criticalIssues; }
    
    public int getHighIssues() { return highIssues; }
    public void setHighIssues(int highIssues) { this.highIssues = highIssues; }
    
    public int getMediumIssues() { return mediumIssues; }
    public void setMediumIssues(int mediumIssues) { this.mediumIssues = mediumIssues; }
    
    public int getLowIssues() { return lowIssues; }
    public void setLowIssues(int lowIssues) { this.lowIssues = lowIssues; }
    
    public double getOverallScore() { return overallScore; }
    public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
    
    public double getSecurityScore() { return securityScore; }
    public void setSecurityScore(double securityScore) { this.securityScore = securityScore; }
    
    public double getProcessScore() { return processScore; }
    public void setProcessScore(double processScore) { this.processScore = processScore; }
    
    public double getIntegrationScore() { return integrationScore; }
    public void setIntegrationScore(double integrationScore) { this.integrationScore = integrationScore; }
    
    public List<String> getSummary() { return summary; }
    public void setSummary(List<String> summary) { this.summary = summary; }
    
    public List<String> getKeyFindings() { return keyFindings; }
    public void setKeyFindings(List<String> keyFindings) { this.keyFindings = keyFindings; }
    
    public List<String> getRiskFactors() { return riskFactors; }
    public void setRiskFactors(List<String> riskFactors) { this.riskFactors = riskFactors; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
    
    public String getAnalyzerVersion() { return analyzerVersion; }
    public void setAnalyzerVersion(String analyzerVersion) { this.analyzerVersion = analyzerVersion; }
    
    // Вспомогательные методы
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    public boolean isFailed() {
        return "FAILED".equals(status);
    }
    
    public boolean hasOpenApiAnalysis() {
        return openApiAnalysis != null;
    }
    
    public boolean hasBpmnAnalysis() {
        return bpmnAnalysis != null;
    }
    
    public boolean hasIntegratedAnalysis() {
        return integratedAnalysis != null;
    }
    
    public void addSummaryPoint(String point) {
        if (summary == null) {
            summary = new java.util.ArrayList<>();
        }
        summary.add(point);
    }
    
    public void addKeyFinding(String finding) {
        if (keyFindings == null) {
            keyFindings = new java.util.ArrayList<>();
        }
        keyFindings.add(finding);
    }
    
    public void addRiskFactor(String risk) {
        if (riskFactors == null) {
            riskFactors = new java.util.ArrayList<>();
        }
        riskFactors.add(risk);
    }
    
    @Override
    public String toString() {
        return "ComprehensiveAnalysisResult{" +
                "analysisId='" + analysisId + '\'' +
                ", projectId='" + projectId + '\'' +
                ", status='" + status + '\'' +
                ", totalIssues=" + totalIssues +
                ", overallScore=" + overallScore +
                ", processingTimeMs=" + processingTimeMs +
                '}';
    }
}