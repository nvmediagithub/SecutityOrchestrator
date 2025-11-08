package org.example.domain.dto.context;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Объединенный контекст для генерации данных
 */
public class UnifiedContext {
    
    private String contextId;
    private LocalDateTime createdAt;
    private BpmnContext bpmnContext;
    private ApiContext apiContext;
    private CrossSystemContext crossSystemContext;
    private List<ContextElement> contextElements;
    private ContextMetrics contextMetrics;
    private int contextCount;
    
    public UnifiedContext() {
        this.contextId = "unified_" + System.currentTimeMillis();
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getContextId() { return contextId; }
    public void setContextId(String contextId) { this.contextId = contextId; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public BpmnContext getBpmnContext() { return bpmnContext; }
    public void setBpmnContext(BpmnContext bpmnContext) { this.bpmnContext = bpmnContext; }
    
    public ApiContext getApiContext() { return apiContext; }
    public void setApiContext(ApiContext apiContext) { this.apiContext = apiContext; }
    
    public CrossSystemContext getCrossSystemContext() { return crossSystemContext; }
    public void setCrossSystemContext(CrossSystemContext crossSystemContext) { this.crossSystemContext = crossSystemContext; }
    
    public List<ContextElement> getContextElements() { return contextElements; }
    public void setContextElements(List<ContextElement> contextElements) { this.contextElements = contextElements; }
    
    public ContextMetrics getContextMetrics() { return contextMetrics; }
    public void setContextMetrics(ContextMetrics contextMetrics) { this.contextMetrics = contextMetrics; }
    
    public int getContextCount() { 
        return contextElements != null ? contextElements.size() : 0; 
    }
    
    public void setContextCount(int contextCount) { this.contextCount = contextCount; }
}