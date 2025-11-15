package org.example.domain.entities.analysis;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * Результат анализа безопасности для API, BPMN процессов и системы в целом
 */
public class SecurityAnalysisResult extends AnalysisResult {
    private final List<SecurityIssue> securityIssues;
    private final Map<String, Integer> vulnerabilityByCategory;
    private final Set<String> criticalEndpoints;
    private final List<String> securityRecommendations;
    private final Integer totalVulnerabilities;
    private final Integer highSeverityCount;
    private final Integer mediumSeverityCount;
    private final Integer lowSeverityCount;
    private final String securityScore;
    private final List<String> complianceStatus;
    private final Map<String, Boolean> securityControls;
    private final String riskLevel;
    
    public SecurityAnalysisResult(Builder builder) {
        super(builder);
        this.securityIssues = builder.securityIssues != null ? builder.securityIssues : new ArrayList<>();
        this.vulnerabilityByCategory = builder.vulnerabilityByCategory != null ? builder.vulnerabilityByCategory : new HashMap<>();
        this.criticalEndpoints = builder.criticalEndpoints != null ? builder.criticalEndpoints : new HashSet<>();
        this.securityRecommendations = builder.securityRecommendations != null ? builder.securityRecommendations : new ArrayList<>();
        this.totalVulnerabilities = builder.totalVulnerabilities != null ? builder.totalVulnerabilities : 0;
        this.highSeverityCount = builder.highSeverityCount != null ? builder.highSeverityCount : 0;
        this.mediumSeverityCount = builder.mediumSeverityCount != null ? builder.mediumSeverityCount : 0;
        this.lowSeverityCount = builder.lowSeverityCount != null ? builder.lowSeverityCount : 0;
        this.securityScore = builder.securityScore != null ? builder.securityScore : "UNKNOWN";
        this.complianceStatus = builder.complianceStatus != null ? builder.complianceStatus : new ArrayList<>();
        this.securityControls = builder.securityControls != null ? builder.securityControls : new HashMap<>();
        this.riskLevel = builder.riskLevel != null ? builder.riskLevel : "UNKNOWN";
    }
    
    // Getters
    public List<SecurityIssue> getSecurityIssues() {
        return securityIssues;
    }
    
    public Map<String, Integer> getVulnerabilityByCategory() {
        return vulnerabilityByCategory;
    }
    
    public Set<String> getCriticalEndpoints() {
        return criticalEndpoints;
    }
    
    public List<String> getSecurityRecommendations() {
        return securityRecommendations;
    }
    
    public Integer getTotalVulnerabilities() {
        return totalVulnerabilities;
    }
    
    public Integer getHighSeverityCount() {
        return highSeverityCount;
    }
    
    public Integer getMediumSeverityCount() {
        return mediumSeverityCount;
    }
    
    public Integer getLowSeverityCount() {
        return lowSeverityCount;
    }
    
    public String getSecurityScore() {
        return securityScore;
    }
    
    public List<String> getComplianceStatus() {
        return complianceStatus;
    }
    
    public Map<String, Boolean> getSecurityControls() {
        return securityControls;
    }
    
    public String getRiskLevel() {
        return riskLevel;
    }
    
    // Utility methods
    public boolean hasSecurityIssues() {
        return !securityIssues.isEmpty();
    }
    
    public boolean hasCriticalVulnerabilities() {
        return highSeverityCount > 0;
    }
    
    public boolean isSecurityAcceptable() {
        return "A".equals(securityScore) || "B".equals(securityScore);
    }
    
    public boolean isHighRisk() {
        return "HIGH".equals(riskLevel) || "CRITICAL".equals(riskLevel);
    }
    
    public int getCriticalVulnerabilityCount() {
        return (int) securityIssues.stream()
            .filter(issue -> "CRITICAL".equals(issue.getSeverity()))
            .count();
    }
    
    public List<SecurityIssue> getCriticalIssues() {
        return securityIssues.stream()
            .filter(issue -> "CRITICAL".equals(issue.getSeverity()))
            .toList();
    }
    
    public boolean isCompliant() {
        return complianceStatus.stream().allMatch(status -> "COMPLIANT".equals(status));
    }
    
    // Inner classes
    public static class SecurityIssue {
        private final String id;
        private final String type;
        private final String severity;
        private final String category;
        private final String description;
        private final String affectedComponent;
        private final String cveId;
        private final String remediation;
        private final String owaspCategory;
        private final boolean isExploitable;
        private final String impact;
        
        public SecurityIssue(String id, String type, String severity, String category,
                           String description, String affectedComponent, String cveId,
                           String remediation, String owaspCategory, boolean isExploitable, String impact) {
            this.id = id;
            this.type = type;
            this.severity = severity;
            this.category = category;
            this.description = description;
            this.affectedComponent = affectedComponent;
            this.cveId = cveId;
            this.remediation = remediation;
            this.owaspCategory = owaspCategory;
            this.isExploitable = isExploitable;
            this.impact = impact;
        }
        
        // Getters
        public String getId() { return id; }
        public String getType() { return type; }
        public String getSeverity() { return severity; }
        public String getCategory() { return category; }
        public String getDescription() { return description; }
        public String getAffectedComponent() { return affectedComponent; }
        public String getCveId() { return cveId; }
        public String getRemediation() { return remediation; }
        public String getOwaspCategory() { return owaspCategory; }
        public boolean isExploitable() { return isExploitable; }
        public String getImpact() { return impact; }
        
        public boolean isCritical() {
            return "CRITICAL".equals(severity);
        }
        
        public boolean isHighPriority() {
            return "HIGH".equals(severity) || "CRITICAL".equals(severity);
        }
        
        public boolean isOwaspTop10() {
            return owaspCategory != null && !owaspCategory.trim().isEmpty();
        }
    }
    
    public static class Builder extends AnalysisResult.Builder<Builder> {
        private List<SecurityIssue> securityIssues;
        private Map<String, Integer> vulnerabilityByCategory;
        private Set<String> criticalEndpoints;
        private List<String> securityRecommendations;
        private Integer totalVulnerabilities;
        private Integer highSeverityCount;
        private Integer mediumSeverityCount;
        private Integer lowSeverityCount;
        private String securityScore;
        private List<String> complianceStatus;
        private Map<String, Boolean> securityControls;
        private String riskLevel;
        
        public Builder() {
            super();
        }
        
        @Override
        public Builder status(String status) {
            return super.status(status != null ? status : "SECURITY_ANALYSIS");
        }
        
        public Builder securityIssues(List<SecurityIssue> securityIssues) {
            this.securityIssues = securityIssues;
            return this;
        }
        
        public Builder vulnerabilityByCategory(Map<String, Integer> vulnerabilityByCategory) {
            this.vulnerabilityByCategory = vulnerabilityByCategory;
            return this;
        }
        
        public Builder criticalEndpoints(Set<String> criticalEndpoints) {
            this.criticalEndpoints = criticalEndpoints;
            return this;
        }
        
        public Builder securityRecommendations(List<String> securityRecommendations) {
            this.securityRecommendations = securityRecommendations;
            return this;
        }
        
        public Builder totalVulnerabilities(Integer totalVulnerabilities) {
            this.totalVulnerabilities = totalVulnerabilities;
            return this;
        }
        
        public Builder highSeverityCount(Integer highSeverityCount) {
            this.highSeverityCount = highSeverityCount;
            return this;
        }
        
        public Builder mediumSeverityCount(Integer mediumSeverityCount) {
            this.mediumSeverityCount = mediumSeverityCount;
            return this;
        }
        
        public Builder lowSeverityCount(Integer lowSeverityCount) {
            this.lowSeverityCount = lowSeverityCount;
            return this;
        }
        
        public Builder securityScore(String securityScore) {
            this.securityScore = securityScore;
            return this;
        }
        
        public Builder complianceStatus(List<String> complianceStatus) {
            this.complianceStatus = complianceStatus;
            return this;
        }
        
        public Builder securityControls(Map<String, Boolean> securityControls) {
            this.securityControls = securityControls;
            return this;
        }
        
        public Builder riskLevel(String riskLevel) {
            this.riskLevel = riskLevel;
            return this;
        }
        
        @Override
        protected Builder self() {
            return this;
        }
        
        @Override
        public SecurityAnalysisResult build() {
            return new SecurityAnalysisResult(this);
        }
    }
}