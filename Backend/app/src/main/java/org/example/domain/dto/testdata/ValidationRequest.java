package org.example.domain.dto.testdata;

import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;

/**
 * DTO for test data validation request
 * Used for POST /api/data/validate
 */
public class ValidationRequest {
    
    @NotBlank(message = "Validation request ID is required")
    private String requestId;
    
    @NotEmpty(message = "Data records cannot be empty")
    private List<Map<String, Object>> dataRecords;
    
    @NotEmpty(message = "Validation rules cannot be empty")
    private List<String> validationRules;
    
    @Size(max = 10, message = "Maximum 10 custom validations allowed")
    private List<CustomValidation> customValidations;
    
    private String dataType;
    private String scope; // GLOBAL, PROJECT, MODULE, TEST_CASE
    private String complianceStandard; // GDPR, HIPAA, SOX, etc.
    
    private boolean enableDeepValidation = false;
    private boolean enableCrossFieldValidation = true;
    private boolean enableBusinessRuleValidation = true;
    
    @Min(value = 1, message = "Timeout must be at least 1 minute")
    @Max(value = 60, message = "Timeout cannot exceed 60 minutes")
    private int timeoutMinutes = 10;
    
    private Map<String, Object> context;
    private List<String> tags;
    private String createdBy;
    
    // Constructors
    public ValidationRequest() {}
    
    public ValidationRequest(String requestId, List<Map<String, Object>> dataRecords, List<String> validationRules) {
        this.requestId = requestId;
        this.dataRecords = dataRecords;
        this.validationRules = validationRules;
    }
    
    // Getters and Setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public List<Map<String, Object>> getDataRecords() { return dataRecords; }
    public void setDataRecords(List<Map<String, Object>> dataRecords) { this.dataRecords = dataRecords; }
    
    public List<String> getValidationRules() { return validationRules; }
    public void setValidationRules(List<String> validationRules) { this.validationRules = validationRules; }
    
    public List<CustomValidation> getCustomValidations() { return customValidations; }
    public void setCustomValidations(List<CustomValidation> customValidations) { this.customValidations = customValidations; }
    
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    
    public String getComplianceStandard() { return complianceStandard; }
    public void setComplianceStandard(String complianceStandard) { this.complianceStandard = complianceStandard; }
    
    public boolean isEnableDeepValidation() { return enableDeepValidation; }
    public void setEnableDeepValidation(boolean enableDeepValidation) { this.enableDeepValidation = enableDeepValidation; }
    
    public boolean isEnableCrossFieldValidation() { return enableCrossFieldValidation; }
    public void setEnableCrossFieldValidation(boolean enableCrossFieldValidation) { this.enableCrossFieldValidation = enableCrossFieldValidation; }
    
    public boolean isEnableBusinessRuleValidation() { return enableBusinessRuleValidation; }
    public void setEnableBusinessRuleValidation(boolean enableBusinessRuleValidation) { this.enableBusinessRuleValidation = enableBusinessRuleValidation; }
    
    public int getTimeoutMinutes() { return timeoutMinutes; }
    public void setTimeoutMinutes(int timeoutMinutes) { this.timeoutMinutes = timeoutMinutes; }
    
    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    // Helper methods
    public int getRecordCount() {
        return dataRecords != null ? dataRecords.size() : 0;
    }
    
    public boolean hasCustomValidations() {
        return customValidations != null && !customValidations.isEmpty();
    }
    
    public boolean isComplianceRequired() {
        return complianceStandard != null && !complianceStandard.trim().isEmpty();
    }
    
    public void addTag(String tag) {
        if (tags == null) {
            tags = new java.util.ArrayList<>();
        }
        if (tag != null && !tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void addCustomValidation(CustomValidation validation) {
        if (customValidations == null) {
            customValidations = new java.util.ArrayList<>();
        }
        if (validation != null) {
            customValidations.add(validation);
        }
    }
    
    // Inner class for custom validations
    public static class CustomValidation {
        @NotBlank(message = "Custom validation name is required")
        private String name;
        
        @NotBlank(message = "Custom validation expression is required")
        private String expression;
        
        private String description;
        private String errorMessage;
        private ValidationSeverity severity = ValidationSeverity.ERROR;
        
        public CustomValidation() {}
        
        public CustomValidation(String name, String expression) {
            this.name = name;
            this.expression = expression;
        }
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getExpression() { return expression; }
        public void setExpression(String expression) { this.expression = expression; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public ValidationSeverity getSeverity() { return severity; }
        public void setSeverity(ValidationSeverity severity) { this.severity = severity; }
    }
    
    public enum ValidationSeverity {
        INFO, WARNING, ERROR, CRITICAL
    }
    
    @Override
    public String toString() {
        return "ValidationRequest{" +
                "requestId='" + requestId + '\'' +
                ", dataType='" + dataType + '\'' +
                ", recordCount=" + getRecordCount() +
                ", validationRules=" + (validationRules != null ? validationRules.size() : 0) +
                '}';
    }
}