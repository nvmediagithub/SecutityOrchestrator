package org.example.infrastructure.llm.testdata.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for test data quality analysis
 */
public class TestDataQualityReport {
    
    private String analysisId;
    private LocalDateTime analyzedAt;
    private double overallScore; // 0-100
    private List<String> issues;
    private List<String> recommendations;
    private QualityDimensions dimensions;
    
    public static class QualityDimensions {
        private double completeness; // How complete the data is
        private double accuracy;     // How accurate the data is
        private double consistency;  // How consistent the data is
        private double validity;     // How valid the data is
        private double uniqueness;   // How unique the data is
        private double timeliness;   // How timely/recent the data is
        
        // Getters and Setters
        public double getCompleteness() { return completeness; }
        public void setCompleteness(double completeness) { this.completeness = completeness; }
        
        public double getAccuracy() { return accuracy; }
        public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
        
        public double getConsistency() { return consistency; }
        public void setConsistency(double consistency) { this.consistency = consistency; }
        
        public double getValidity() { return validity; }
        public void setValidity(double validity) { this.validity = validity; }
        
        public double getUniqueness() { return uniqueness; }
        public void setUniqueness(double uniqueness) { this.uniqueness = uniqueness; }
        
        public double getTimeliness() { return timeliness; }
        public void setTimeliness(double timeliness) { this.timeliness = timeliness; }
    }
    
    // Constructors
    public TestDataQualityReport() {
        this.dimensions = new QualityDimensions();
    }
    
    // Getters and Setters
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
    
    public double getOverallScore() { return overallScore; }
    public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
    
    public List<String> getIssues() { return issues; }
    public void setIssues(List<String> issues) { this.issues = issues; }
    
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    
    public QualityDimensions getDimensions() { return dimensions; }
    public void setDimensions(QualityDimensions dimensions) { this.dimensions = dimensions; }
    
    // Helper methods
    public boolean hasIssues() {
        return issues != null && !issues.isEmpty();
    }
    
    public boolean hasRecommendations() {
        return recommendations != null && !recommendations.isEmpty();
    }
    
    public boolean isHighQuality() {
        return overallScore >= 80.0;
    }
    
    public boolean isAcceptableQuality() {
        return overallScore >= 60.0;
    }
    
    public boolean isPoorQuality() {
        return overallScore < 60.0;
    }
    
    public String getQualityGrade() {
        if (overallScore >= 90) return "A";
        if (overallScore >= 80) return "B";
        if (overallScore >= 70) return "C";
        if (overallScore >= 60) return "D";
        return "F";
    }
    
    @Override
    public String toString() {
        return "TestDataQualityReport{" +
                "analysisId='" + analysisId + '\'' +
                ", overallScore=" + overallScore +
                ", qualityGrade='" + getQualityGrade() + '\'' +
                ", issuesCount=" + (issues != null ? issues.size() : 0) +
                '}';
    }
}