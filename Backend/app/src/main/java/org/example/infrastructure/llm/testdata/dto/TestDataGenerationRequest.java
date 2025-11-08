package org.example.infrastructure.llm.testdata.dto;

import org.example.domain.model.testdata.enums.GenerationScope;
import org.example.domain.model.testdata.enums.DataType;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;

/**
 * DTO for LLM test data generation request
 */
public class TestDataGenerationRequest {
    
    @NotBlank(message = "Request ID is required")
    private String requestId;
    
    @NotBlank(message = "Data type is required")
    private String dataType;
    
    @NotNull(message = "Generation scope is required")
    private GenerationScope generationScope;
    
    @Min(value = 1, message = "Record count must be at least 1")
    @Max(value = 10000, message = "Record count cannot exceed 10000")
    private int recordCount;
    
    private Map<String, Object> constraints;
    private List<String> requiredFields;
    private List<String> excludeFields;
    
    @Pattern(regexp = "^(BASIC|STANDARD|PREMIUM|ENTERPRISE)$", 
             message = "Quality level must be one of: BASIC, STANDARD, PREMIUM, ENTERPRISE")
    private String qualityLevel = "STANDARD";
    
    private Map<String, Object> context;
    private String templateId;
    private String profileId;
    
    private boolean enableValidation = true;
    private boolean enableDependencyAnalysis = false;
    private boolean preserveContext = false;
    private List<String> validationRules;
    private boolean enableBatchProcessing = false;
    
    @Min(value = 10, message = "Batch size must be at least 10")
    @Max(value = 1000, message = "Batch size cannot exceed 1000")
    private int batchSize = 100;
    
    private boolean enableLLM = true;
    
    @Min(value = 1, message = "Timeout must be at least 1 minute")
    @Max(value = 60, message = "Timeout cannot exceed 60 minutes")
    private int timeoutMinutes = 15;
    
    // Business context for generation
    private String businessDomain; // FINANCIAL, ECOMMERCE, HEALTHCARE, etc.
    private String userProfile; // CUSTOMER, BUSINESS_USER, ADMIN, etc.
    private Map<String, Object> financialLimits; // min, max amounts
    private boolean securityTestData = false;
    private String owaspTestType; // SQL_INJECTION, XSS, AUTH_BYPASS, etc.
    
    // Constructors
    public TestDataGenerationRequest() {
        this.requestId = "llm_gen_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }
    
    public TestDataGenerationRequest(String dataType, GenerationScope generationScope, int recordCount) {
        this();
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
    
    public String getProfileId() { return profileId; }
    public void setProfileId(String profileId) { this.profileId = profileId; }
    
    public boolean isEnableValidation() { return enableValidation; }
    public void setEnableValidation(boolean enableValidation) { this.enableValidation = enableValidation; }
    
    public boolean isEnableDependencyAnalysis() { return enableDependencyAnalysis; }
    public void setEnableDependencyAnalysis(boolean enableDependencyAnalysis) { this.enableDependencyAnalysis = enableDependencyAnalysis; }
    
    public boolean isPreserveContext() { return preserveContext; }
    public void setPreserveContext(boolean preserveContext) { this.preserveContext = preserveContext; }
    
    public List<String> getValidationRules() { return validationRules; }
    public void setValidationRules(List<String> validationRules) { this.validationRules = validationRules; }
    
    public boolean isEnableBatchProcessing() { return enableBatchProcessing; }
    public void setEnableBatchProcessing(boolean enableBatchProcessing) { this.enableBatchProcessing = enableBatchProcessing; }
    
    public int getBatchSize() { return batchSize; }
    public void setBatchSize(int batchSize) { this.batchSize = batchSize; }
    
    public boolean isEnableLLM() { return enableLLM; }
    public void setEnableLLM(boolean enableLLM) { this.enableLLM = enableLLM; }
    
    public int getTimeoutMinutes() { return timeoutMinutes; }
    public void setTimeoutMinutes(int timeoutMinutes) { this.timeoutMinutes = timeoutMinutes; }
    
    public String getBusinessDomain() { return businessDomain; }
    public void setBusinessDomain(String businessDomain) { this.businessDomain = businessDomain; }
    
    public String getUserProfile() { return userProfile; }
    public void setUserProfile(String userProfile) { this.userProfile = userProfile; }
    
    public Map<String, Object> getFinancialLimits() { return financialLimits; }
    public void setFinancialLimits(Map<String, Object> financialLimits) { this.financialLimits = financialLimits; }
    
    public boolean isSecurityTestData() { return securityTestData; }
    public void setSecurityTestData(boolean securityTestData) { this.securityTestData = securityTestData; }
    
    public String getOwaspTestType() { return owaspTestType; }
    public void setOwaspTestType(String owaspTestType) { this.owaspTestType = owaspTestType; }
    
    // Helper methods
    public boolean isHighQuality() {
        return "PREMIUM".equalsIgnoreCase(qualityLevel) || "ENTERPRISE".equalsIgnoreCase(qualityLevel);
    }
    
    public boolean isEnterprise() {
        return "ENTERPRISE".equalsIgnoreCase(qualityLevel);
    }
    
    public boolean needsBatchProcessing() {
        return enableBatchProcessing || recordCount > batchSize;
    }
    
    public boolean hasBusinessContext() {
        return businessDomain != null || userProfile != null;
    }
    
    public boolean isFinancialData() {
        return "FINANCIAL".equalsIgnoreCase(businessDomain);
    }
}