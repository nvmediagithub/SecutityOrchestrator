package org.example.domain.dto.context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Контекст API спецификаций
 */
public class ApiContext {
    
    private String analysisId;
    private LocalDateTime analyzedAt;
    private List<ApiEndpointContext> endpointContexts;
    private List<ApiDataSchema> dataSchemas;
    private ParameterAnalysis parameterAnalysis;
    private BusinessContext businessContext;
    
    public ApiContext() {
        this.analysisId = "api_ctx_" + System.currentTimeMillis();
        this.analyzedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
    
    public List<ApiEndpointContext> getEndpointContexts() { return endpointContexts; }
    public void setEndpointContexts(List<ApiEndpointContext> endpointContexts) { this.endpointContexts = endpointContexts; }
    
    public List<ApiDataSchema> getDataSchemas() { return dataSchemas; }
    public void setDataSchemas(List<ApiDataSchema> dataSchemas) { this.dataSchemas = dataSchemas; }
    
    public ParameterAnalysis getParameterAnalysis() { return parameterAnalysis; }
    public void setParameterAnalysis(ParameterAnalysis parameterAnalysis) { this.parameterAnalysis = parameterAnalysis; }
    
    public BusinessContext getBusinessContext() { return businessContext; }
    public void setBusinessContext(BusinessContext businessContext) { this.businessContext = businessContext; }
}