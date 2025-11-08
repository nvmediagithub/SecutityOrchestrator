package org.example.domain.dto.reports;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for dashboard metrics used in frontend visualization
 */
public class DashboardMetrics {
    
    @JsonProperty
    private UUID id;
    
    @JsonProperty
    private String diagramId;
    
    @JsonProperty
    private ProcessMetrics processMetrics;
    
    @JsonProperty
    private SecurityMetrics securityMetrics;
    
    @JsonProperty
    private ComplianceMetrics complianceMetrics;
    
    @JsonProperty
    private PerformanceMetricsData performanceMetrics;
    
    @JsonProperty
    private Map<String, Object> summary;
    
    @JsonProperty
    private String generatedAt;
    
    @JsonProperty
    private String lastUpdated;
    
    public DashboardMetrics() {}
    
    public DashboardMetrics(String diagramId) {
        this.id = UUID.randomUUID();
        this.diagramId = diagramId;
        this.generatedAt = java.time.LocalDateTime.now().toString();
        this.lastUpdated = java.time.LocalDateTime.now().toString();
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getDiagramId() {
        return diagramId;
    }
    
    public void setDiagramId(String diagramId) {
        this.diagramId = diagramId;
    }
    
    public ProcessMetrics getProcessMetrics() {
        return processMetrics;
    }
    
    public void setProcessMetrics(ProcessMetrics processMetrics) {
        this.processMetrics = processMetrics;
    }
    
    public SecurityMetrics getSecurityMetrics() {
        return securityMetrics;
    }
    
    public void setSecurityMetrics(SecurityMetrics securityMetrics) {
        this.securityMetrics = securityMetrics;
    }
    
    public ComplianceMetrics getComplianceMetrics() {
        return complianceMetrics;
    }
    
    public void setComplianceMetrics(ComplianceMetrics complianceMetrics) {
        this.complianceMetrics = complianceMetrics;
    }
    
    public PerformanceMetricsData getPerformanceMetrics() {
        return performanceMetrics;
    }
    
    public void setPerformanceMetrics(PerformanceMetricsData performanceMetrics) {
        this.performanceMetrics = performanceMetrics;
    }
    
    public Map<String, Object> getSummary() {
        return summary;
    }
    
    public void setSummary(Map<String, Object> summary) {
        this.summary = summary;
    }
    
    public String getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }
    
    public String getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    /**
     * Process-related metrics
     */
    public static class ProcessMetrics {
        @JsonProperty
        private int totalProcesses;
        
        @JsonProperty
        private int activeProcesses;
        
        @JsonProperty
        private int completedProcesses;
        
        @JsonProperty
        private int failedProcesses;
        
        @JsonProperty
        private double averageCompletionTime;
        
        @JsonProperty
        private Map<String, Integer> processDistribution;
        
        @JsonProperty
        private List<String> processTypes;
        
        // Getters and Setters
        public int getTotalProcesses() {
            return totalProcesses;
        }
        
        public void setTotalProcesses(int totalProcesses) {
            this.totalProcesses = totalProcesses;
        }
        
        public int getActiveProcesses() {
            return activeProcesses;
        }
        
        public void setActiveProcesses(int activeProcesses) {
            this.activeProcesses = activeProcesses;
        }
        
        public int getCompletedProcesses() {
            return completedProcesses;
        }
        
        public void setCompletedProcesses(int completedProcesses) {
            this.completedProcesses = completedProcesses;
        }
        
        public int getFailedProcesses() {
            return failedProcesses;
        }
        
        public void setFailedProcesses(int failedProcesses) {
            this.failedProcesses = failedProcesses;
        }
        
        public double getAverageCompletionTime() {
            return averageCompletionTime;
        }
        
        public void setAverageCompletionTime(double averageCompletionTime) {
            this.averageCompletionTime = averageCompletionTime;
        }
        
        public Map<String, Integer> getProcessDistribution() {
            return processDistribution;
        }
        
        public void setProcessDistribution(Map<String, Integer> processDistribution) {
            this.processDistribution = processDistribution;
        }
        
        public List<String> getProcessTypes() {
            return processTypes;
        }
        
        public void setProcessTypes(List<String> processTypes) {
            this.processTypes = processTypes;
        }
    }
    
    /**
     * Security-related metrics
     */
    public static class SecurityMetrics {
        @JsonProperty
        private int totalSecurityChecks;
        
        @JsonProperty
        private int passedChecks;
        
        @JsonProperty
        private int failedChecks;
        
        @JsonProperty
        private int criticalIssues;
        
        @JsonProperty
        private int highPriorityIssues;
        
        @JsonProperty
        private int mediumPriorityIssues;
        
        @JsonProperty
        private int lowPriorityIssues;
        
        @JsonProperty
        private double securityScore;
        
        @JsonProperty
        private Map<String, String> vulnerabilityTypes;
        
        // Getters and Setters
        public int getTotalSecurityChecks() {
            return totalSecurityChecks;
        }
        
        public void setTotalSecurityChecks(int totalSecurityChecks) {
            this.totalSecurityChecks = totalSecurityChecks;
        }
        
        public int getPassedChecks() {
            return passedChecks;
        }
        
        public void setPassedChecks(int passedChecks) {
            this.passedChecks = passedChecks;
        }
        
        public int getFailedChecks() {
            return failedChecks;
        }
        
        public void setFailedChecks(int failedChecks) {
            this.failedChecks = failedChecks;
        }
        
        public int getCriticalIssues() {
            return criticalIssues;
        }
        
        public void setCriticalIssues(int criticalIssues) {
            this.criticalIssues = criticalIssues;
        }
        
        public int getHighPriorityIssues() {
            return highPriorityIssues;
        }
        
        public void setHighPriorityIssues(int highPriorityIssues) {
            this.highPriorityIssues = highPriorityIssues;
        }
        
        public int getMediumPriorityIssues() {
            return mediumPriorityIssues;
        }
        
        public void setMediumPriorityIssues(int mediumPriorityIssues) {
            this.mediumPriorityIssues = mediumPriorityIssues;
        }
        
        public int getLowPriorityIssues() {
            return lowPriorityIssues;
        }
        
        public void setLowPriorityIssues(int lowPriorityIssues) {
            this.lowPriorityIssues = lowPriorityIssues;
        }
        
        public double getSecurityScore() {
            return securityScore;
        }
        
        public void setSecurityScore(double securityScore) {
            this.securityScore = securityScore;
        }
        
        public Map<String, String> getVulnerabilityTypes() {
            return vulnerabilityTypes;
        }
        
        public void setVulnerabilityTypes(Map<String, String> vulnerabilityTypes) {
            this.vulnerabilityTypes = vulnerabilityTypes;
        }
    }
    
    /**
     * Compliance-related metrics
     */
    public static class ComplianceMetrics {
        @JsonProperty
        private double overallComplianceScore;
        
        @JsonProperty
        private Map<String, Double> complianceByStandard;
        
        @JsonProperty
        private int totalComplianceChecks;
        
        @JsonProperty
        private int passedComplianceChecks;
        
        @JsonProperty
        private int failedComplianceChecks;
        
        @JsonProperty
        private List<String> complianceStandards;
        
        @JsonProperty
        private Map<String, Object> complianceGaps;
        
        // Getters and Setters
        public double getOverallComplianceScore() {
            return overallComplianceScore;
        }
        
        public void setOverallComplianceScore(double overallComplianceScore) {
            this.overallComplianceScore = overallComplianceScore;
        }
        
        public Map<String, Double> getComplianceByStandard() {
            return complianceByStandard;
        }
        
        public void setComplianceByStandard(Map<String, Double> complianceByStandard) {
            this.complianceByStandard = complianceByStandard;
        }
        
        public int getTotalComplianceChecks() {
            return totalComplianceChecks;
        }
        
        public void setTotalComplianceChecks(int totalComplianceChecks) {
            this.totalComplianceChecks = totalComplianceChecks;
        }
        
        public int getPassedComplianceChecks() {
            return passedComplianceChecks;
        }
        
        public void setPassedComplianceChecks(int passedComplianceChecks) {
            this.passedComplianceChecks = passedComplianceChecks;
        }
        
        public int getFailedComplianceChecks() {
            return failedComplianceChecks;
        }
        
        public void setFailedComplianceChecks(int failedComplianceChecks) {
            this.failedComplianceChecks = failedComplianceChecks;
        }
        
        public List<String> getComplianceStandards() {
            return complianceStandards;
        }
        
        public void setComplianceStandards(List<String> complianceStandards) {
            this.complianceStandards = complianceStandards;
        }
        
        public Map<String, Object> getComplianceGaps() {
            return complianceGaps;
        }
        
        public void setComplianceGaps(Map<String, Object> complianceGaps) {
            this.complianceGaps = complianceGaps;
        }
    }
    
    /**
     * Performance-related metrics
     */
    public static class PerformanceMetricsData {
        @JsonProperty
        private double averageResponseTime;
        
        @JsonProperty
        private double peakResponseTime;
        
        @JsonProperty
        private int totalRequests;
        
        @JsonProperty
        private double throughput;
        
        @JsonProperty
        private double errorRate;
        
        @JsonProperty
        private Map<String, Object> resourceUtilization;
        
        @JsonProperty
        private List<String> performanceTrends;
        
        // Getters and Setters
        public double getAverageResponseTime() {
            return averageResponseTime;
        }
        
        public void setAverageResponseTime(double averageResponseTime) {
            this.averageResponseTime = averageResponseTime;
        }
        
        public double getPeakResponseTime() {
            return peakResponseTime;
        }
        
        public void setPeakResponseTime(double peakResponseTime) {
            this.peakResponseTime = peakResponseTime;
        }
        
        public int getTotalRequests() {
            return totalRequests;
        }
        
        public void setTotalRequests(int totalRequests) {
            this.totalRequests = totalRequests;
        }
        
        public double getThroughput() {
            return throughput;
        }
        
        public void setThroughput(double throughput) {
            this.throughput = throughput;
        }
        
        public double getErrorRate() {
            return errorRate;
        }
        
        public void setErrorRate(double errorRate) {
            this.errorRate = errorRate;
        }
        
        public Map<String, Object> getResourceUtilization() {
            return resourceUtilization;
        }
        
        public void setResourceUtilization(Map<String, Object> resourceUtilization) {
            this.resourceUtilization = resourceUtilization;
        }
        
        public List<String> getPerformanceTrends() {
            return performanceTrends;
        }
        
        public void setPerformanceTrends(List<String> performanceTrends) {
            this.performanceTrends = performanceTrends;
        }
    }
}