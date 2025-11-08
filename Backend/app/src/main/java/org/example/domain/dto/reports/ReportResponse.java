package org.example.domain.dto.reports;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.domain.entities.ApiProcessGap;
import org.example.domain.entities.ProcessIssue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO containing generated report data
 */
public class ReportResponse {
    
    @JsonProperty
    private UUID id;
    
    @JsonProperty
    private String reportName;
    
    @JsonProperty
    private String reportType;
    
    @JsonProperty
    private String format;
    
    @JsonProperty
    private LocalDateTime generatedAt;
    
    @JsonProperty
    private String generatedBy;
    
    @JsonProperty
    private ReportContent content;
    
    @JsonProperty
    private List<String> warnings;
    
    @JsonProperty
    private List<String> errors;
    
    @JsonProperty
    private Map<String, Object> metadata;
    
    @JsonProperty
    private String downloadUrl;
    
    public ReportResponse() {}
    
    public ReportResponse(UUID id, String reportName, String reportType) {
        this.id = id;
        this.reportName = reportName;
        this.reportType = reportType;
        this.generatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getReportName() {
        return reportName;
    }
    
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
    
    public String getReportType() {
        return reportType;
    }
    
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
    
    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
    
    public String getGeneratedBy() {
        return generatedBy;
    }
    
    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }
    
    public ReportContent getContent() {
        return content;
    }
    
    public void setContent(ReportContent content) {
        this.content = content;
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
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public String getDownloadUrl() {
        return downloadUrl;
    }
    
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    
    /**
     * Inner class containing the actual report data
     */
    public static class ReportContent {
        
        @JsonProperty
        private Map<String, Object> executiveSummary;
        
        @JsonProperty
        private Map<String, Object> processApiMapping;
        
        @JsonProperty
        private Map<String, Object> businessAnalysis;
        
        @JsonProperty
        private Map<String, Object> complianceAnalysis;
        
        @JsonProperty
        private Map<String, Object> securityAssessment;
        
        @JsonProperty
        private Map<String, Object> complianceReport;
        
        @JsonProperty
        private Map<String, Object> rawData;
        
        @JsonProperty
        private List<ProcessIssue> issues;
        
        @JsonProperty
        private List<ApiProcessGap> gaps;
        
        @JsonProperty
        private Map<String, Object> metrics;
        
        // Getters and Setters
        public Map<String, Object> getExecutiveSummary() {
            return executiveSummary;
        }
        
        public void setExecutiveSummary(Map<String, Object> executiveSummary) {
            this.executiveSummary = executiveSummary;
        }
        
        public Map<String, Object> getProcessApiMapping() {
            return processApiMapping;
        }
        
        public void setProcessApiMapping(Map<String, Object> processApiMapping) {
            this.processApiMapping = processApiMapping;
        }
        
        public Map<String, Object> getBusinessAnalysis() {
            return businessAnalysis;
        }
        
        public void setBusinessAnalysis(Map<String, Object> businessAnalysis) {
            this.businessAnalysis = businessAnalysis;
        }
        
        public Map<String, Object> getComplianceAnalysis() {
            return complianceAnalysis;
        }
        
        public void setComplianceAnalysis(Map<String, Object> complianceAnalysis) {
            this.complianceAnalysis = complianceAnalysis;
        }
        
        public Map<String, Object> getSecurityAssessment() {
            return securityAssessment;
        }
        
        public void setSecurityAssessment(Map<String, Object> securityAssessment) {
            this.securityAssessment = securityAssessment;
        }
        
        public Map<String, Object> getComplianceReport() {
            return complianceReport;
        }
        
        public void setComplianceReport(Map<String, Object> complianceReport) {
            this.complianceReport = complianceReport;
        }
        
        public Map<String, Object> getRawData() {
            return rawData;
        }
        
        public void setRawData(Map<String, Object> rawData) {
            this.rawData = rawData;
        }
        
        public List<ProcessIssue> getIssues() {
            return issues;
        }
        
        public void setIssues(List<ProcessIssue> issues) {
            this.issues = issues;
        }
        
        public List<ApiProcessGap> getGaps() {
            return gaps;
        }
        
        public void setGaps(List<ApiProcessGap> gaps) {
            this.gaps = gaps;
        }
        
        public Map<String, Object> getMetrics() {
            return metrics;
        }
        
        public void setMetrics(Map<String, Object> metrics) {
            this.metrics = metrics;
        }
    }
}