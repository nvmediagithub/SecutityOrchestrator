package org.example.infrastructure.llm.testdata.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Quality report for generated test data
 */
public class TestDataQualityReport {
    
    private String analysisId;
    private int overallScore; // 0-100
    private String qualityGrade; // A, B, C, D, F
    private List<String> recommendations;
    private List<String> issues;
    private List<String> warnings;
    private List<String> strengths;
    private double completenessScore;
    private double consistencyScore;
    private double realismScore;
    private double complianceScore;
    private LocalDateTime analyzedAt;
    private String analyzerVersion;
    
    // Constructors
    public TestDataQualityReport() {}
    
    // Getters and Setters
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }
    
    public String getQualityGrade() { return qualityGrade; }
    public void setQualityGrade(String qualityGrade) { this.qualityGrade = qualityGrade; }
    
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    
    public List<String> getIssues() { return issues; }
    public void setIssues(List<String> issues) { this.issues = issues; }
    
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    
    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }
    
    public double getCompletenessScore() { return completenessScore; }
    public void setCompletenessScore(double completenessScore) { this.completenessScore = completenessScore; }
    
    public double getConsistencyScore() { return consistencyScore; }
    public void setConsistencyScore(double consistencyScore) { this.consistencyScore = consistencyScore; }
    
    public double getRealismScore() { return realismScore; }
    public void setRealismScore(double realismScore) { this.realismScore = realismScore; }
    
    public double getComplianceScore() { return complianceScore; }
    public void setComplianceScore(double complianceScore) { this.complianceScore = complianceScore; }
    
    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
    
    public String getAnalyzerVersion() { return analyzerVersion; }
    public void setAnalyzerVersion(String analyzerVersion) { this.analyzerVersion = analyzerVersion; }
}