package org.example.domain.dto.reports;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.domain.valueobjects.BpmnProcessId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for generating various types of BPMN analysis reports
 */
public class ReportRequest {
    
    @JsonProperty
    private UUID id;
    
    @JsonProperty
    private BpmnProcessId diagramId;
    
    @JsonProperty
    private ReportType reportType;
    
    @JsonProperty
    private ReportFormat format;
    
    @JsonProperty
    private List<String> sections;
    
    @JsonProperty
    private LocalDateTime fromDate;
    
    @JsonProperty
    private LocalDateTime toDate;
    
    @JsonProperty
    private boolean includeMetrics;
    
    @JsonProperty
    private boolean includeVisualizations;
    
    @JsonProperty
    private ReportLevel level;
    
    @JsonProperty
    private String template;
    
    @JsonProperty
    private List<String> stakeholders;
    
    public ReportRequest() {}
    
    public ReportRequest(BpmnProcessId diagramId, ReportType reportType, ReportFormat format) {
        this.id = UUID.randomUUID();
        this.diagramId = diagramId;
        this.reportType = reportType;
        this.format = format;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public BpmnProcessId getDiagramId() {
        return diagramId;
    }
    
    public void setDiagramId(BpmnProcessId diagramId) {
        this.diagramId = diagramId;
    }
    
    public ReportType getReportType() {
        return reportType;
    }
    
    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }
    
    public ReportFormat getFormat() {
        return format;
    }
    
    public void setFormat(ReportFormat format) {
        this.format = format;
    }
    
    public List<String> getSections() {
        return sections;
    }
    
    public void setSections(List<String> sections) {
        this.sections = sections;
    }
    
    public LocalDateTime getFromDate() {
        return fromDate;
    }
    
    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }
    
    public LocalDateTime getToDate() {
        return toDate;
    }
    
    public void setToDate(LocalDateTime toDate) {
        this.toDate = toDate;
    }
    
    public boolean isIncludeMetrics() {
        return includeMetrics;
    }
    
    public void setIncludeMetrics(boolean includeMetrics) {
        this.includeMetrics = includeMetrics;
    }
    
    public boolean isIncludeVisualizations() {
        return includeVisualizations;
    }
    
    public void setIncludeVisualizations(boolean includeVisualizations) {
        this.includeVisualizations = includeVisualizations;
    }
    
    public ReportLevel getLevel() {
        return level;
    }
    
    public void setLevel(ReportLevel level) {
        this.level = level;
    }
    
    public String getTemplate() {
        return template;
    }
    
    public void setTemplate(String template) {
        this.template = template;
    }
    
    public List<String> getStakeholders() {
        return stakeholders;
    }
    
    public void setStakeholders(List<String> stakeholders) {
        this.stakeholders = stakeholders;
    }
    
    public enum ReportType {
        PROCESS_API_MAPPING,
        BUSINESS_PROCESS_ANALYSIS,
        API_PROCESS_COMPLIANCE,
        PROCESS_SECURITY_ASSESSMENT,
        COMPLIANCE_REPORT,
        EXECUTIVE_SUMMARY,
        TECHNICAL_REPORT,
        BUSINESS_REPORT,
        COMPREHENSIVE_ANALYSIS
    }
    
    public enum ReportFormat {
        PDF, HTML, JSON, CSV, XML
    }
    
    public enum ReportLevel {
        EXECUTIVE, TACTICAL, TECHNICAL, DETAILED
    }
}