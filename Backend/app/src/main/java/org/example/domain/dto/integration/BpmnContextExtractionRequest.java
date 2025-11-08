package org.example.domain.dto.integration;

import java.util.List;
import java.util.Map;

/**
 * Запрос на извлечение контекста из BPMN процессов
 */
public class BpmnContextExtractionRequest {
    
    private String diagramId;
    private String bpmnContent;
    private String format; // xml, json, auto
    private List<String> extractionTypes; // PROCESS_VARIABLES, TASKS, GATEWAYS, EVENTS, etc.
    private Map<String, Object> extractionOptions;
    private boolean includeTaskData;
    private boolean includeGatewayConditions;
    private boolean includeEventData;
    private boolean includeBusinessRules;
    private boolean analyzeDataFlows;
    
    public BpmnContextExtractionRequest() {}
    
    public BpmnContextExtractionRequest(String diagramId, String bpmnContent, String format) {
        this.diagramId = diagramId;
        this.bpmnContent = bpmnContent;
        this.format = format;
    }
    
    // Getters and Setters
    public String getDiagramId() { return diagramId; }
    public void setDiagramId(String diagramId) { this.diagramId = diagramId; }
    
    public String getBpmnContent() { return bpmnContent; }
    public void setBpmnContent(String bpmnContent) { this.bpmnContent = bpmnContent; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    
    public List<String> getExtractionTypes() { return extractionTypes; }
    public void setExtractionTypes(List<String> extractionTypes) { this.extractionTypes = extractionTypes; }
    
    public Map<String, Object> getExtractionOptions() { return extractionOptions; }
    public void setExtractionOptions(Map<String, Object> extractionOptions) { this.extractionOptions = extractionOptions; }
    
    public boolean isIncludeTaskData() { return includeTaskData; }
    public void setIncludeTaskData(boolean includeTaskData) { this.includeTaskData = includeTaskData; }
    
    public boolean isIncludeGatewayConditions() { return includeGatewayConditions; }
    public void setIncludeGatewayConditions(boolean includeGatewayConditions) { this.includeGatewayConditions = includeGatewayConditions; }
    
    public boolean isIncludeEventData() { return includeEventData; }
    public void setIncludeEventData(boolean includeEventData) { this.includeEventData = includeEventData; }
    
    public boolean isIncludeBusinessRules() { return includeBusinessRules; }
    public void setIncludeBusinessRules(boolean includeBusinessRules) { this.includeBusinessRules = includeBusinessRules; }
    
    public boolean isAnalyzeDataFlows() { return analyzeDataFlows; }
    public void setAnalyzeDataFlows(boolean analyzeDataFlows) { this.analyzeDataFlows = analyzeDataFlows; }
}