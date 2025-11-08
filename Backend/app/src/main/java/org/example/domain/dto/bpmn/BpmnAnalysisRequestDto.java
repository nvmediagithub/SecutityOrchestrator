package org.example.domain.dto.bpmn;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Request DTO for BPMN analysis initiation
 * Used for POST /api/analysis/bpmn/{diagramId} endpoint
 */
public class BpmnAnalysisRequestDto {
    
    @NotBlank(message = "Diagram ID is required")
    private String diagramId;
    
    @NotBlank(message = "Diagram name is required")
    private String diagramName;
    
    @NotBlank(message = "BPMN content is required")
    private String bpmnContent;
    
    private String description;
    private String version;
    
    @NotNull(message = "Analysis type must be specified")
    private List<AnalysisType> analysisTypes;
    
    private Map<String, Object> analysisOptions;
    private boolean asyncProcessing;
    private Long timeoutMinutes;
    private List<String> includeChecks;
    private List<String> excludeChecks;
    
    public BpmnAnalysisRequestDto() {}
    
    public BpmnAnalysisRequestDto(String diagramId, String diagramName, String bpmnContent, 
                                 String description, String version,
                                 List<AnalysisType> analysisTypes, Map<String, Object> analysisOptions,
                                 boolean asyncProcessing, Long timeoutMinutes,
                                 List<String> includeChecks, List<String> excludeChecks) {
        this.diagramId = diagramId;
        this.diagramName = diagramName;
        this.bpmnContent = bpmnContent;
        this.description = description;
        this.version = version;
        this.analysisTypes = analysisTypes;
        this.analysisOptions = analysisOptions;
        this.asyncProcessing = asyncProcessing;
        this.timeoutMinutes = timeoutMinutes;
        this.includeChecks = includeChecks;
        this.excludeChecks = excludeChecks;
    }
    
    public enum AnalysisType {
        STRUCTURE,
        SECURITY,
        PERFORMANCE,
        COMPREHENSIVE,
        COMPLIANCE,
        API_MAPPING
    }
    
    // Validation method
    public boolean isValid() {
        return diagramId != null && !diagramId.trim().isEmpty() &&
               diagramName != null && !diagramName.trim().isEmpty() &&
               bpmnContent != null && !bpmnContent.trim().isEmpty() &&
               (analysisTypes == null || analysisTypes.isEmpty() || 
                analysisTypes.stream().anyMatch(type -> 
                    type == AnalysisType.STRUCTURE || 
                    type == AnalysisType.SECURITY || 
                    type == AnalysisType.PERFORMANCE || 
                    type == AnalysisType.COMPREHENSIVE));
    }
    
    // Getters and setters
    public String getDiagramId() { return diagramId; }
    public void setDiagramId(String diagramId) { this.diagramId = diagramId; }
    
    public String getDiagramName() { return diagramName; }
    public void setDiagramName(String diagramName) { this.diagramName = diagramName; }
    
    public String getBpmnContent() { return bpmnContent; }
    public void setBpmnContent(String bpmnContent) { this.bpmnContent = bpmnContent; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public List<AnalysisType> getAnalysisTypes() { return analysisTypes; }
    public void setAnalysisTypes(List<AnalysisType> analysisTypes) { this.analysisTypes = analysisTypes; }
    
    public Map<String, Object> getAnalysisOptions() { return analysisOptions; }
    public void setAnalysisOptions(Map<String, Object> analysisOptions) { this.analysisOptions = analysisOptions; }
    
    public boolean isAsyncProcessing() { return asyncProcessing; }
    public void setAsyncProcessing(boolean asyncProcessing) { this.asyncProcessing = asyncProcessing; }
    
    public Long getTimeoutMinutes() { return timeoutMinutes; }
    public void setTimeoutMinutes(Long timeoutMinutes) { this.timeoutMinutes = timeoutMinutes; }
    
    public List<String> getIncludeChecks() { return includeChecks; }
    public void setIncludeChecks(List<String> includeChecks) { this.includeChecks = includeChecks; }
    
    public List<String> getExcludeChecks() { return excludeChecks; }
    public void setExcludeChecks(List<String> excludeChecks) { this.excludeChecks = excludeChecks; }
}