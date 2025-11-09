package org.example.infrastructure.services.integrated.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for comprehensive analysis dashboard data
 */
public class ComprehensiveDashboardData {
    
    private UUID dashboardId;
    private String projectId;
    private String projectName;
    private DashboardSummary summary;
    private List<ApiBpmnContradiction> recentContradictions;
    private List<PrioritizedIssue> topIssues;
    private Map<String, Object> metrics;
    private List<String> recommendations;
    private String lastAnalysisTimestamp;
    private DashboardStatus status;
    
    public ComprehensiveDashboardData() {
    }
    
    public ComprehensiveDashboardData(String projectId, String projectName) {
        this.dashboardId = UUID.randomUUID();
        this.projectId = projectId;
        this.projectName = projectName;
    }
    
    public static class DashboardSummary {
        private int totalContradictions;
        private int criticalIssues;
        private int resolvedIssues;
        private double overallHealthScore;
        private int openApiEndpoints;
        private int bpmnElements;
        private String lastAnalysisDate;
        
        public DashboardSummary() {
        }
        
        public int getTotalContradictions() {
            return totalContradictions;
        }
        
        public void setTotalContradictions(int totalContradictions) {
            this.totalContradictions = totalContradictions;
        }
        
        public int getCriticalIssues() {
            return criticalIssues;
        }
        
        public void setCriticalIssues(int criticalIssues) {
            this.criticalIssues = criticalIssues;
        }
        
        public int getResolvedIssues() {
            return resolvedIssues;
        }
        
        public void setResolvedIssues(int resolvedIssues) {
            this.resolvedIssues = resolvedIssues;
        }
        
        public double getOverallHealthScore() {
            return overallHealthScore;
        }
        
        public void setOverallHealthScore(double overallHealthScore) {
            this.overallHealthScore = overallHealthScore;
        }
        
        public int getOpenApiEndpoints() {
            return openApiEndpoints;
        }
        
        public void setOpenApiEndpoints(int openApiEndpoints) {
            this.openApiEndpoints = openApiEndpoints;
        }
        
        public int getBpmnElements() {
            return bpmnElements;
        }
        
        public void setBpmnElements(int bpmnElements) {
            this.bpmnElements = bpmnElements;
        }
        
        public String getLastAnalysisDate() {
            return lastAnalysisDate;
        }
        
        public void setLastAnalysisDate(String lastAnalysisDate) {
            this.lastAnalysisDate = lastAnalysisDate;
        }
    }
    
    public enum DashboardStatus {
        HEALTHY, WARNING, CRITICAL, UNKNOWN
    }
    
    // Getters and Setters
    public UUID getDashboardId() {
        return dashboardId;
    }
    
    public void setDashboardId(UUID dashboardId) {
        this.dashboardId = dashboardId;
    }
    
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public DashboardSummary getSummary() {
        return summary;
    }
    
    public void setSummary(DashboardSummary summary) {
        this.summary = summary;
    }
    
    public List<ApiBpmnContradiction> getRecentContradictions() {
        return recentContradictions;
    }
    
    public void setRecentContradictions(List<ApiBpmnContradiction> recentContradictions) {
        this.recentContradictions = recentContradictions;
    }
    
    public List<PrioritizedIssue> getTopIssues() {
        return topIssues;
    }
    
    public void setTopIssues(List<PrioritizedIssue> topIssues) {
        this.topIssues = topIssues;
    }
    
    public Map<String, Object> getMetrics() {
        return metrics;
    }
    
    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }
    
    public List<String> getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
    
    public String getLastAnalysisTimestamp() {
        return lastAnalysisTimestamp;
    }
    
    public void setLastAnalysisTimestamp(String lastAnalysisTimestamp) {
        this.lastAnalysisTimestamp = lastAnalysisTimestamp;
    }
    
    public DashboardStatus getStatus() {
        return status;
    }
    
    public void setStatus(DashboardStatus status) {
        this.status = status;
    }
}