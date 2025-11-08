package org.example.domain.model.consistency;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO запроса на проверку консистентности
 */
public class ConsistencyCheckRequest {
    
    private String checkId;
    private String apiSpecId;
    private String bpmnDiagramId;
    private String llmAnalysisId;
    private List<String> checkTypes;
    private String validationLevel;
    private LocalDateTime checkStartTime;
    private LocalDateTime checkEndTime;
    private String userId;
    private boolean useCache;
    
    // Результаты анализа из других сервисов
    private Object apiAnalysisResult;
    private Object bpmnAnalysisResult;
    private Object crossReferenceResult;
    
    public ConsistencyCheckRequest() {
        this.checkStartTime = LocalDateTime.now();
    }
    
    public ConsistencyCheckRequest(String apiSpecId, String bpmnDiagramId, List<String> checkTypes) {
        this();
        this.apiSpecId = apiSpecId;
        this.bpmnDiagramId = bpmnDiagramId;
        this.checkTypes = checkTypes;
        this.validationLevel = "STANDARD";
        this.useCache = true;
    }
    
    // Getters and Setters
    public String getCheckId() { return checkId; }
    public void setCheckId(String checkId) { this.checkId = checkId; }
    
    public String getApiSpecId() { return apiSpecId; }
    public void setApiSpecId(String apiSpecId) { this.apiSpecId = apiSpecId; }
    
    public String getBpmnDiagramId() { return bpmnDiagramId; }
    public void setBpmnDiagramId(String bpmnDiagramId) { this.bpmnDiagramId = bpmnDiagramId; }
    
    public String getLlmAnalysisId() { return llmAnalysisId; }
    public void setLlmAnalysisId(String llmAnalysisId) { this.llmAnalysisId = llmAnalysisId; }
    
    public List<String> getCheckTypes() { return checkTypes; }
    public void setCheckTypes(List<String> checkTypes) { this.checkTypes = checkTypes; }
    
    public String getValidationLevel() { return validationLevel; }
    public void setValidationLevel(String validationLevel) { this.validationLevel = validationLevel; }
    
    public LocalDateTime getCheckStartTime() { return checkStartTime; }
    public void setCheckStartTime(LocalDateTime checkStartTime) { this.checkStartTime = checkStartTime; }
    
    public LocalDateTime getCheckEndTime() { return checkEndTime; }
    public void setCheckEndTime(LocalDateTime checkEndTime) { this.checkEndTime = checkEndTime; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public boolean isUseCache() { return useCache; }
    public void setUseCache(boolean useCache) { this.useCache = useCache; }
    
    public Object getApiAnalysisResult() { return apiAnalysisResult; }
    public void setApiAnalysisResult(Object apiAnalysisResult) { this.apiAnalysisResult = apiAnalysisResult; }
    
    public Object getBpmnAnalysisResult() { return bpmnAnalysisResult; }
    public void setBpmnAnalysisResult(Object bpmnAnalysisResult) { this.bpmnAnalysisResult = bpmnAnalysisResult; }
    
    public Object getCrossReferenceResult() { return crossReferenceResult; }
    public void setCrossReferenceResult(Object crossReferenceResult) { this.crossReferenceResult = crossReferenceResult; }
}