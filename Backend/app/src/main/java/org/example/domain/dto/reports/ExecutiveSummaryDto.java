package org.example.domain.dto.reports;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for executive summary data used in reports
 */
public class ExecutiveSummaryDto {
    
    @JsonProperty
    private UUID reportId;
    
    @JsonProperty
    private String title;
    
    @JsonProperty
    private String description;
    
    @JsonProperty
    private int totalProcesses;
    
    @JsonProperty
    private int totalApiEndpoints;
    
    @JsonProperty
    private int totalIssues;
    
    @JsonProperty
    private int criticalIssues;
    
    @JsonProperty
    private int complianceScore;
    
    @JsonProperty
    private String overallStatus;
    
    @JsonProperty
    private List<String> keyFindings;
    
    @JsonProperty
    private List<String> recommendations;
    
    @JsonProperty
    private Map<String, Object> riskMetrics;
    
    @JsonProperty
    private Map<String, Integer> issueDistribution;
    
    @JsonProperty
    private String generatedAt;
    
    public ExecutiveSummaryDto() {}
    
    public ExecutiveSummaryDto(String title, String description) {
        this.title = title;
        this.description = description;
        this.generatedAt = java.time.LocalDateTime.now().toString();
    }
    
    // Getters and Setters
    public UUID getReportId() {
        return reportId;
    }
    
    public void setReportId(UUID reportId) {
        this.reportId = reportId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getTotalProcesses() {
        return totalProcesses;
    }
    
    public void setTotalProcesses(int totalProcesses) {
        this.totalProcesses = totalProcesses;
    }
    
    public int getTotalApiEndpoints() {
        return totalApiEndpoints;
    }
    
    public void setTotalApiEndpoints(int totalApiEndpoints) {
        this.totalApiEndpoints = totalApiEndpoints;
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
    
    public int getComplianceScore() {
        return complianceScore;
    }
    
    public void setComplianceScore(int complianceScore) {
        this.complianceScore = complianceScore;
    }
    
    public String getOverallStatus() {
        return overallStatus;
    }
    
    public void setOverallStatus(String overallStatus) {
        this.overallStatus = overallStatus;
    }
    
    public List<String> getKeyFindings() {
        return keyFindings;
    }
    
    public void setKeyFindings(List<String> keyFindings) {
        this.keyFindings = keyFindings;
    }
    
    public List<String> getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
    
    public Map<String, Object> getRiskMetrics() {
        return riskMetrics;
    }
    
    public void setRiskMetrics(Map<String, Object> riskMetrics) {
        this.riskMetrics = riskMetrics;
    }
    
    public Map<String, Integer> getIssueDistribution() {
        return issueDistribution;
    }
    
    public void setIssueDistribution(Map<String, Integer> issueDistribution) {
        this.issueDistribution = issueDistribution;
    }
    
    public String getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }
}