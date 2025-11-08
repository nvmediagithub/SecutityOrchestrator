package org.example.domain.dto.context;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Контекст BPMN процессов
 */
public class BpmnContext {
    
    private String analysisId;
    private LocalDateTime analyzedAt;
    private List<BpmnProcessContext> processContexts;
    private List<ContextDependency> contextualDependencies;
    private Map<String, DataFlowMapping> dataFlowMapping;
    
    public BpmnContext() {
        this.analysisId = "bpmn_ctx_" + System.currentTimeMillis();
        this.analyzedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
    
    public List<BpmnProcessContext> getProcessContexts() { return processContexts; }
    public void setProcessContexts(List<BpmnProcessContext> processContexts) { this.processContexts = processContexts; }
    
    public List<ContextDependency> getContextualDependencies() { return contextualDependencies; }
    public void setContextualDependencies(List<ContextDependency> contextualDependencies) { this.contextualDependencies = contextualDependencies; }
    
    public Map<String, DataFlowMapping> getDataFlowMapping() { return dataFlowMapping; }
    public void setDataFlowMapping(Map<String, DataFlowMapping> dataFlowMapping) { this.dataFlowMapping = dataFlowMapping; }
}