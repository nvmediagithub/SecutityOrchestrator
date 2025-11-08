package org.example.domain.dto.integration;

import java.util.List;
import java.util.Map;

/**
 * Запрос на проверку согласованности данных
 */
public class DataConsistencyCheckRequest {
    
    private String apiSpecId;
    private String bpmnDiagramId;
    private OpenApiDataAnalysisResult apiAnalysisResult;
    private BpmnContextExtractionResult bpmnAnalysisResult;
    private CrossReferenceMappingResult crossReferenceResult;
    private List<String> checkTypes; // API_BPMN_CONSISTENCY, SCHEMA_ALIGNMENT, BUSINESS_RULE_CONSISTENCY, etc.
    private Map<String, Object> checkOptions;
    private boolean validateDataFlows;
    private boolean validateBusinessRules;
    private boolean validateApiSecurity;
    private boolean validateProcessSecurity;
    private String validationLevel; // STRICT, MODERATE, LENIENT
    
    public DataConsistencyCheckRequest() {}
    
    public DataConsistencyCheckRequest(String apiSpecId, String bpmnDiagramId) {
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
    
    public CrossReferenceMappingResult getCrossReferenceResult() { return crossReferenceResult; }
    public void setCrossReferenceResult(CrossReferenceMappingResult crossReferenceResult) { this.crossReferenceResult = crossReferenceResult; }
    
    public List<String> getCheckTypes() { return checkTypes; }
    public void setCheckTypes(List<String> checkTypes) { this.checkTypes = checkTypes; }
    
    public Map<String, Object> getCheckOptions() { return checkOptions; }
    public void setCheckOptions(Map<String, Object> checkOptions) { this.checkOptions = checkOptions; }
    
    public boolean isValidateDataFlows() { return validateDataFlows; }
    public void setValidateDataFlows(boolean validateDataFlows) { this.validateDataFlows = validateDataFlows; }
    
    public boolean isValidateBusinessRules() { return validateBusinessRules; }
    public void setValidateBusinessRules(boolean validateBusinessRules) { this.validateBusinessRules = validateBusinessRules; }
    
    public boolean isValidateApiSecurity() { return validateApiSecurity; }
    public void setValidateApiSecurity(boolean validateApiSecurity) { this.validateApiSecurity = validateApiSecurity; }
    
    public boolean isValidateProcessSecurity() { return validateProcessSecurity; }
    public void setValidateProcessSecurity(boolean validateProcessSecurity) { this.validateProcessSecurity = validateProcessSecurity; }
    
    public String getValidationLevel() { return validationLevel; }
    public void setValidationLevel(String validationLevel) { this.validationLevel = validationLevel; }
}