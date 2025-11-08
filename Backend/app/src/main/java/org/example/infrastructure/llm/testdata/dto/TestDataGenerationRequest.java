package org.example.infrastructure.llm.testdata.dto;

import org.example.domain.model.testdata.enums.GenerationScope;

import java.util.Map;
import java.util.List;

/**
 * Request object for test data generation
 */
public class TestDataGenerationRequest {
    
    private String requestId;
    private String dataType; // personal, financial, security, etc.
    private GenerationScope generationScope;
    private int recordCount;
    private Map<String, Object> constraints;
    private List<String> requiredFields;
    private List<String> excludeFields;
    private String qualityLevel; // BASIC, STANDARD, PREMIUM, ENTERPRISE
    private Map<String, Object> context;
    private String templateId;
    private boolean enableValidation;
    private boolean enableDependencyAnalysis;
    private boolean preserveContext;
    
    // Constructors
    public TestDataGenerationRequest() {}
    
    public TestDataGenerationRequest(String requestId, String dataType, GenerationScope generationScope, int recordCount) {
        this.requestId = requestId;
        this.dataType = dataType;
        this.generationScope = generationScope;
        this.recordCount = recordCount;
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
    
    public Map<String, Object> getConstraints() { return constraints; }
    public void setConstraints(Map<String, Object> constraints) { this.constraints = constraints; }
    
    public List<String> getRequiredFields() { return requiredFields; }
    public void setRequiredFields(List<String> requiredFields) { this.requiredFields = requiredFields; }
    
    public List<String> getExcludeFields() { return excludeFields; }
    public void setExcludeFields(List<String> excludeFields) { this.excludeFields = excludeFields; }
    
    public String getQualityLevel() { return qualityLevel; }
    public void setQualityLevel(String qualityLevel) { this.qualityLevel = qualityLevel; }
    
    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
    
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    
    public boolean isEnableValidation() { return enableValidation; }
    public void setEnableValidation(boolean enableValidation) { this.enableValidation = enableValidation; }
    
    public boolean isEnableDependencyAnalysis() { return enableDependencyAnalysis; }
    public void setEnableDependencyAnalysis(boolean enableDependencyAnalysis) { this.enableDependencyAnalysis = enableDependencyAnalysis; }
    
    public boolean isPreserveContext() { return preserveContext; }
    public void setPreserveContext(boolean preserveContext) { this.preserveContext = preserveContext; }
}