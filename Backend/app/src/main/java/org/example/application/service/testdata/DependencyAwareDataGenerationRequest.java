package org.example.application.service.testdata;

import org.example.domain.model.testdata.enums.GenerationScope;

import java.util.List;
import java.util.Map;

/**
 * Request object for dependency-aware test data generation
 */
public class DependencyAwareDataGenerationRequest {
    
    private String requestId;
    private String dataType;
    private GenerationScope generationScope;
    private int recordCount;
    private Map<String, Object> context;
    private String qualityLevel;
    private List<String> dependencyIds;
    private List<String> chainIds;
    private boolean enableComplexChains;
    private boolean enableOptimization;
    private Map<String, Object> constraints;
    private List<String> requiredFields;
    private List<String> excludeFields;
    private String templateId;
    private boolean enableValidation;
    private boolean enableDependencyAnalysis;
    private boolean preserveContext;
    
    // Constructors
    public DependencyAwareDataGenerationRequest() {}
    
    public DependencyAwareDataGenerationRequest(String requestId, String dataType, GenerationScope generationScope) {
        this.requestId = requestId;
        this.dataType = dataType;
        this.generationScope = generationScope;
    }
    
    // Getters and Setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    
    public GenerationScope getGenerationScope() { return generationScope; }
    public void setGenerationScope(GenerationScope generationScope) { this.generationScope = generationScope; }
    
    public int getRecordCount() { return recordCount; }
    public void setRecordCount(int recordCount) { this.recordCount = recordCount; }
    
    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
    
    public String getQualityLevel() { return qualityLevel; }
    public void setQualityLevel(String qualityLevel) { this.qualityLevel = qualityLevel; }
    
    public List<String> getDependencyIds() { return dependencyIds; }
    public void setDependencyIds(List<String> dependencyIds) { this.dependencyIds = dependencyIds; }
    
    public List<String> getChainIds() { return chainIds; }
    public void setChainIds(List<String> chainIds) { this.chainIds = chainIds; }
    
    public boolean isEnableComplexChains() { return enableComplexChains; }
    public void setEnableComplexChains(boolean enableComplexChains) { this.enableComplexChains = enableComplexChains; }
    
    public boolean isEnableOptimization() { return enableOptimization; }
    public void setEnableOptimization(boolean enableOptimization) { this.enableOptimization = enableOptimization; }
    
    public Map<String, Object> getConstraints() { return constraints; }
    public void setConstraints(Map<String, Object> constraints) { this.constraints = constraints; }
    
    public List<String> getRequiredFields() { return requiredFields; }
    public void setRequiredFields(List<String> requiredFields) { this.requiredFields = requiredFields; }
    
    public List<String> getExcludeFields() { return excludeFields; }
    public void setExcludeFields(List<String> excludeFields) { this.excludeFields = excludeFields; }
    
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    
    public boolean isEnableValidation() { return enableValidation; }
    public void setEnableValidation(boolean enableValidation) { this.enableValidation = enableValidation; }
    
    public boolean isEnableDependencyAnalysis() { return enableDependencyAnalysis; }
    public void setEnableDependencyAnalysis(boolean enableDependencyAnalysis) { this.enableDependencyAnalysis = enableDependencyAnalysis; }
    
    public boolean isPreserveContext() { return preserveContext; }
    public void setPreserveContext(boolean preserveContext) { this.preserveContext = preserveContext; }
}