package org.example.domain.dto.context;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Request для анализа контекста
 */
public class ContextAnalysisRequest {
    
    private String requestId;
    private String analysisType; // COMPREHENSIVE, BPMN_ONLY, API_ONLY, CROSS_SYSTEM
    private List<String> bpmnDiagramIds;
    private List<String> apiSpecIds;
    private List<String> dataSourceIds;
    private String contextScope; // PROCESS, API, END_TO_END, BUSINESS
    private boolean includeBusinessRules;
    private boolean includeDataFlow;
    private boolean includeUserJourneys;
    private LocalDateTime createdAt;
    
    public ContextAnalysisRequest() {
        this.requestId = "ctx_req_" + System.currentTimeMillis();
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public String getAnalysisType() { return analysisType; }
    public void setAnalysisType(String analysisType) { this.analysisType = analysisType; }
    
    public List<String> getBpmnDiagramIds() { return bpmnDiagramIds; }
    public void setBpmnDiagramIds(List<String> bpmnDiagramIds) { this.bpmnDiagramIds = bpmnDiagramIds; }
    
    public List<String> getApiSpecIds() { return apiSpecIds; }
    public void setApiSpecIds(List<String> apiSpecIds) { this.apiSpecIds = apiSpecIds; }
    
    public List<String> getDataSourceIds() { return dataSourceIds; }
    public void setDataSourceIds(List<String> dataSourceIds) { this.dataSourceIds = dataSourceIds; }
    
    public String getContextScope() { return contextScope; }
    public void setContextScope(String contextScope) { this.contextScope = contextScope; }
    
    public boolean isIncludeBusinessRules() { return includeBusinessRules; }
    public void setIncludeBusinessRules(boolean includeBusinessRules) { this.includeBusinessRules = includeBusinessRules; }
    
    public boolean isIncludeDataFlow() { return includeDataFlow; }
    public void setIncludeDataFlow(boolean includeDataFlow) { this.includeDataFlow = includeDataFlow; }
    
    public boolean isIncludeUserJourneys() { return includeUserJourneys; }
    public void setIncludeUserJourneys(boolean includeUserJourneys) { this.includeUserJourneys = includeUserJourneys; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}