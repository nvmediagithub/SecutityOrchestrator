package org.example.domain.dto.integration;

import java.util.List;
import java.util.Map;

/**
 * Запрос на маппинг между API и BPMN
 */
public class CrossReferenceMappingRequest {
    
    private String apiSpecId;
    private String bpmnDiagramId;
    private OpenApiDataAnalysisResult apiAnalysisResult;
    private BpmnContextExtractionResult bpmnAnalysisResult;
    private List<String> mappingTypes; // ENDPOINT_TASK, DATA_FLOW, BUSINESS_SCENARIO, etc.
    private Map<String, Object> mappingOptions;
    private boolean includeUnmappedItems;
    private boolean generateUnifiedScenarios;
    private boolean createEndToEndFlows;
    
    public CrossReferenceMappingRequest() {}
    
    public CrossReferenceMappingRequest(String apiSpecId, String bpmnDiagramId) {
        this.apiSpecId = apiSpecId;
        this.bpmnDiagramId = bpmnDiagramId;
    }
    
    // Getters and Setters
    public String getApiSpecId() { return apiSpecId; }
    public void setApiSpecId(String apiSpecId) { this.apiSpecId = apiSpecId; }
    
    public String getBpmnDiagramId() { return bpmnDiagramId; }
    public void setBpmnDiagramId(String bpmnDiagramId) { this.bpmnDiagramId = bpmnDiagramId; }
    
    public OpenApiDataAnalysisResult getApiAnalysisResult() { return apiAnalysisResult; }
    public void setApiAnalysisResult(OpenApiDataAnalysisResult apiAnalysisResult) { this.apiAnalysisResult = apiAnalysisResult; }
    
    public BpmnContextExtractionResult getBpmnAnalysisResult() { return bpmnAnalysisResult; }
    public void setBpmnAnalysisResult(BpmnContextExtractionResult bpmnAnalysisResult) { this.bpmnAnalysisResult = bpmnAnalysisResult; }
    
    public List<String> getMappingTypes() { return mappingTypes; }
    public void setMappingTypes(List<String> mappingTypes) { this.mappingTypes = mappingTypes; }
    
    public Map<String, Object> getMappingOptions() { return mappingOptions; }
    public void setMappingOptions(Map<String, Object> mappingOptions) { this.mappingOptions = mappingOptions; }
    
    public boolean isIncludeUnmappedItems() { return includeUnmappedItems; }
    public void setIncludeUnmappedItems(boolean includeUnmappedItems) { this.includeUnmappedItems = includeUnmappedItems; }
    
    public boolean isGenerateUnifiedScenarios() { return generateUnifiedScenarios; }
    public void setGenerateUnifiedScenarios(boolean generateUnifiedScenarios) { this.generateUnifiedScenarios = generateUnifiedScenarios; }
    
    public boolean isCreateEndToEndFlows() { return createEndToEndFlows; }
    public void setCreateEndToEndFlows(boolean createEndToEndFlows) { this.createEndToEndFlows = createEndToEndFlows; }
}