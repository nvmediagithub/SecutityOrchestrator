package org.example.domain.dto.context;

import org.example.domain.model.testdata.ContextAwareData;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Результат анализа контекста
 */
public class ContextAnalysisResult {
    
    private String requestId;
    private String analysisId;
    private UnifiedContext unifiedContext;
    private List<ContextAwareData> contextAwareData;
    private LocalDateTime analysisCompletedAt;
    private boolean success;
    private String errorMessage;
    private ContextAnalysisMetrics metrics;
    
    public ContextAnalysisResult() {
        this.analysisId = "ctx_result_" + System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public UnifiedContext getUnifiedContext() { return unifiedContext; }
    public void setUnifiedContext(UnifiedContext unifiedContext) { this.unifiedContext = unifiedContext; }
    
    public List<ContextAwareData> getContextAwareData() { return contextAwareData; }
    public void setContextAwareData(List<ContextAwareData> contextAwareData) { this.contextAwareData = contextAwareData; }
    
    public LocalDateTime getAnalysisCompletedAt() { return analysisCompletedAt; }
    public void setAnalysisCompletedAt(LocalDateTime analysisCompletedAt) { this.analysisCompletedAt = analysisCompletedAt; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public ContextAnalysisMetrics getMetrics() { return metrics; }
    public void setMetrics(ContextAnalysisMetrics metrics) { this.metrics = metrics; }
    
    // Helper methods
    public boolean isBpmnAnalyzed() {
        return unifiedContext != null && unifiedContext.getBpmnContext() != null;
    }
    
    public boolean isApiAnalyzed() {
        return unifiedContext != null && unifiedContext.getApiContext() != null;
    }
    
    public boolean isCrossSystemAnalyzed() {
        return unifiedContext != null && unifiedContext.getCrossSystemContext() != null;
    }
    
    public int getTotalContextElements() {
        return unifiedContext != null ? unifiedContext.getContextCount() : 0;
    }
    
    public int getContextAwareDataCount() {
        return contextAwareData != null ? contextAwareData.size() : 0;
    }
}