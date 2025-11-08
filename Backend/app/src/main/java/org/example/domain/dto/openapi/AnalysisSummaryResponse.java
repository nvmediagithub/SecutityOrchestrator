package org.example.domain.dto.openapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для краткой сводки результатов анализа OpenAPI
 */
public class AnalysisSummaryResponse {
    
    private String analysisId;
    private String specificationId;
    private String specificationTitle;
    
    // Статистика проблем
    @JsonProperty("totalIssues")
    private int totalIssues;
    
    @JsonProperty("criticalIssues")
    private int criticalIssues;
    
    @JsonProperty("highIssues")
    private int highIssues;
    
    @JsonProperty("mediumIssues")
    private int mediumIssues;
    
    @JsonProperty("lowIssues")
    private int lowIssues;
    
    // Общая оценка
    @JsonProperty("overallScore")
    private double overallScore;
    
    private String grade;
    
    @JsonProperty("analysisTime")
    private String analysisTime;
    
    @JsonProperty("lastAnalyzed")
    private LocalDateTime lastAnalyzed;
    
    @JsonProperty("topRecommendations")
    private List<String> topRecommendations;
    
    // Конструкторы
    public AnalysisSummaryResponse() {}
    
    public AnalysisSummaryResponse(String analysisId, String specificationId, String specificationTitle,
                                 int totalIssues, int criticalIssues, int highIssues, 
                                 int mediumIssues, int lowIssues, double overallScore) {
        this.analysisId = analysisId;
        this.specificationId = specificationId;
        this.specificationTitle = specificationTitle;
        this.totalIssues = totalIssues;
        this.criticalIssues = criticalIssues;
        this.highIssues = highIssues;
        this.mediumIssues = mediumIssues;
        this.lowIssues = lowIssues;
        this.overallScore = overallScore;
        this.lastAnalyzed = LocalDateTime.now();
        this.grade = calculateGrade(overallScore);
    }
    
    // Статические фабричные методы
    public static AnalysisSummaryResponse createMock(String analysisId, String specificationId) {
        AnalysisSummaryResponse response = new AnalysisSummaryResponse();
        response.analysisId = analysisId;
        response.specificationId = specificationId;
        response.specificationTitle = "Mock API Specification";
        response.totalIssues = 12;
        response.criticalIssues = 2;
        response.highIssues = 4;
        response.mediumIssues = 3;
        response.lowIssues = 3;
        response.overallScore = 7.2;
        response.grade = "B";
        response.analysisTime = "2.3s";
        response.lastAnalyzed = LocalDateTime.now();
        response.topRecommendations = List.of(
            "Добавить JWT аутентификацию для защищенных эндпоинтов",
            "Улучшить валидацию входных данных",
            "Стандартизировать соглашения об именовании",
            "Добавить rate limiting описание"
        );
        return response;
    }
    
    public static AnalysisSummaryResponse createEmpty() {
        return new AnalysisSummaryResponse();
    }
    
    // Вспомогательные методы
    private String calculateGrade(double score) {
        if (score >= 9.0) return "A";
        if (score >= 8.0) return "A-";
        if (score >= 7.0) return "B+";
        if (score >= 6.5) return "B";
        if (score >= 5.5) return "C+";
        if (score >= 5.0) return "C";
        if (score >= 4.0) return "D";
        return "F";
    }
    
    // Getters and Setters
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public String getSpecificationId() { return specificationId; }
    public void setSpecificationId(String specificationId) { this.specificationId = specificationId; }
    
    public String getSpecificationTitle() { return specificationTitle; }
    public void setSpecificationTitle(String specificationTitle) { this.specificationTitle = specificationTitle; }
    
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
    public void setOverallScore(double overallScore) { 
        this.overallScore = overallScore;
        this.grade = calculateGrade(overallScore);
    }
    
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    
    public String getAnalysisTime() { return analysisTime; }
    public void setAnalysisTime(String analysisTime) { this.analysisTime = analysisTime; }
    
    public LocalDateTime getLastAnalyzed() { return lastAnalyzed; }
    public void setLastAnalyzed(LocalDateTime lastAnalyzed) { this.lastAnalyzed = lastAnalyzed; }
    
    public List<String> getTopRecommendations() { return topRecommendations; }
    public void setTopRecommendations(List<String> topRecommendations) { this.topRecommendations = topRecommendations; }
    
    @Override
    public String toString() {
        return "AnalysisSummaryResponse{" +
                "analysisId='" + analysisId + '\'' +
                ", specificationId='" + specificationId + '\'' +
                ", specificationTitle='" + specificationTitle + '\'' +
                ", totalIssues=" + totalIssues +
                ", criticalIssues=" + criticalIssues +
                ", highIssues=" + highIssues +
                ", mediumIssues=" + mediumIssues +
                ", lowIssues=" + lowIssues +
                ", overallScore=" + overallScore +
                ", grade='" + grade + '\'' +
                ", analysisTime='" + analysisTime + '\'' +
                ", lastAnalyzed=" + lastAnalyzed +
                ", topRecommendations=" + topRecommendations +
                '}';
    }
}