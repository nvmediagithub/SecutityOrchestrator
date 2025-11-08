package org.example.domain.dto.integration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Результат анализа данных из OpenAPI спецификации
 */
public class OpenApiDataAnalysisResult {
    
    private String analysisId;
    private String specificationId;
    private LocalDateTime analysisStartTime;
    private LocalDateTime analysisEndTime;
    private long processingTimeMs;
    private String status; // SUCCESS, FAILED, PARTIAL
    private String errorMessage;
    
    // Данные схем
    private List<ApiSchema> requestSchemas;
    private List<ApiSchema> responseSchemas;
    private List<ApiSchema> dataModels;
    private Map<String, DataField> dataFields;
    
    // Бизнес-контекст
    private List<ApiEndpoint> apiEndpoints;
    private List<BusinessLogic> businessLogic;
    private List<UserScenario> userScenarios;
    private List<WorkflowPattern> workflowPatterns;
    
    // Валидационные правила
    private List<ValidationRule> validationRules;
    private List<BusinessRule> businessRules;
    private Map<String, DataConstraint> dataConstraints;
    
    // Анализ связей
    private List<ApiRelationship> relationships;
    private Map<String, Object> crossReferences;
    
    // Генерационные шаблоны
    private List<DataTemplate> dataTemplates;
    private Map<String, GenerationRule> generationRules;
    
    // Метрики
    private AnalysisMetrics metrics;
    
    public OpenApiDataAnalysisResult() {
        this.analysisStartTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public String getSpecificationId() { return specificationId; }
    public void setSpecificationId(String specificationId) { this.specificationId = specificationId; }
    
    public LocalDateTime getAnalysisStartTime() { return analysisStartTime; }
    public void setAnalysisStartTime(LocalDateTime analysisStartTime) { this.analysisStartTime = analysisStartTime; }
    
    public LocalDateTime getAnalysisEndTime() { return analysisEndTime; }
    public void setAnalysisEndTime(LocalDateTime analysisEndTime) { this.analysisEndTime = analysisEndTime; }
    
    public long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public List<ApiSchema> getRequestSchemas() { return requestSchemas; }
    public void setRequestSchemas(List<ApiSchema> requestSchemas) { this.requestSchemas = requestSchemas; }
    
    public List<ApiSchema> getResponseSchemas() { return responseSchemas; }
    public void setResponseSchemas(List<ApiSchema> responseSchemas) { this.responseSchemas = responseSchemas; }
    
    public List<ApiSchema> getDataModels() { return dataModels; }
    public void setDataModels(List<ApiSchema> dataModels) { this.dataModels = dataModels; }
    
    public Map<String, DataField> getDataFields() { return dataFields; }
    public void setDataFields(Map<String, DataField> dataFields) { this.dataFields = dataFields; }
    
    public List<ApiEndpoint> getApiEndpoints() { return apiEndpoints; }
    public void setApiEndpoints(List<ApiEndpoint> apiEndpoints) { this.apiEndpoints = apiEndpoints; }
    
    public List<BusinessLogic> getBusinessLogic() { return businessLogic; }
    public void setBusinessLogic(List<BusinessLogic> businessLogic) { this.businessLogic = businessLogic; }
    
    public List<UserScenario> getUserScenarios() { return userScenarios; }
    public void setUserScenarios(List<UserScenario> userScenarios) { this.userScenarios = userScenarios; }
    
    public List<WorkflowPattern> getWorkflowPatterns() { return workflowPatterns; }
    public void setWorkflowPatterns(List<WorkflowPattern> workflowPatterns) { this.workflowPatterns = workflowPatterns; }
    
    public List<ValidationRule> getValidationRules() { return validationRules; }
    public void setValidationRules(List<ValidationRule> validationRules) { this.validationRules = validationRules; }
    
    public List<BusinessRule> getBusinessRules() { return businessRules; }
    public void setBusinessRules(List<BusinessRule> businessRules) { this.businessRules = businessRules; }
    
    public Map<String, DataConstraint> getDataConstraints() { return dataConstraints; }
    public void setDataConstraints(Map<String, DataConstraint> dataConstraints) { this.dataConstraints = dataConstraints; }
    
    public List<ApiRelationship> getRelationships() { return relationships; }
    public void setRelationships(List<ApiRelationship> relationships) { this.relationships = relationships; }
    
    public Map<String, Object> getCrossReferences() { return crossReferences; }
    public void setCrossReferences(Map<String, Object> crossReferences) { this.crossReferences = crossReferences; }
    
    public List<DataTemplate> getDataTemplates() { return dataTemplates; }
    public void setDataTemplates(List<DataTemplate> dataTemplates) { this.dataTemplates = dataTemplates; }
    
    public Map<String, GenerationRule> getGenerationRules() { return generationRules; }
    public void setGenerationRules(Map<String, GenerationRule> generationRules) { this.generationRules = generationRules; }
    
    public AnalysisMetrics getMetrics() { return metrics; }
    public void setMetrics(AnalysisMetrics metrics) { this.metrics = metrics; }
    
    // Вложенные классы для структуры данных
    
    public static class ApiSchema {
        private String name;
        private String type;
        private String description;
        private Map<String, Object> properties;
        private boolean isRequired;
        private List<String> validationRules;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
        
        public boolean isRequired() { return isRequired; }
        public void setRequired(boolean required) { this.isRequired = required; }
        
        public List<String> getValidationRules() { return validationRules; }
        public void setValidationRules(List<String> validationRules) { this.validationRules = validationRules; }
    }
    
    public static class ApiEndpoint {
        private String path;
        private String method;
        private String summary;
        private String description;
        private List<String> parameters;
        private List<String> tags;
        private String businessPurpose;
        
        // Getters and Setters
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getParameters() { return parameters; }
        public void setParameters(List<String> parameters) { this.parameters = parameters; }
        
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        
        public String getBusinessPurpose() { return businessPurpose; }
        public void setBusinessPurpose(String businessPurpose) { this.businessPurpose = businessPurpose; }
    }
    
    public static class BusinessLogic {
        private String name;
        private String type;
        private String description;
        private List<String> steps;
        private List<String> conditions;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getSteps() { return steps; }
        public void setSteps(List<String> steps) { this.steps = steps; }
        
        public List<String> getConditions() { return conditions; }
        public void setConditions(List<String> conditions) { this.conditions = conditions; }
    }
    
    public static class DataField {
        private String name;
        private String type;
        private boolean isRequired;
        private String format;
        private String description;
        private List<String> examples;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public boolean isRequired() { return isRequired; }
        public void setRequired(boolean required) { this.isRequired = required; }
        
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getExamples() { return examples; }
        public void setExamples(List<String> examples) { this.examples = examples; }
    }
    
    public static class ValidationRule {
        private String name;
        private String field;
        private String type;
        private String description;
        private Map<String, Object> parameters;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    }
    
    public static class BusinessRule {
        private String name;
        private String type;
        private String description;
        private List<String> conditions;
        private List<String> actions;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getConditions() { return conditions; }
        public void setConditions(List<String> conditions) { this.conditions = conditions; }
        
        public List<String> getActions() { return actions; }
        public void setActions(List<String> actions) { this.actions = actions; }
    }
    
    public static class DataConstraint {
        private String field;
        private String constraintType;
        private String description;
        private Map<String, Object> parameters;
        
        // Getters and Setters
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        
        public String getConstraintType() { return constraintType; }
        public void setConstraintType(String constraintType) { this.constraintType = constraintType; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    }
    
    public static class ApiRelationship {
        private String fromEndpoint;
        private String toEndpoint;
        private String relationshipType;
        private String description;
        
        // Getters and Setters
        public String getFromEndpoint() { return fromEndpoint; }
        public void setFromEndpoint(String fromEndpoint) { this.fromEndpoint = fromEndpoint; }
        
        public String getToEndpoint() { return toEndpoint; }
        public void setToEndpoint(String toEndpoint) { this.toEndpoint = toEndpoint; }
        
        public String getRelationshipType() { return relationshipType; }
        public void setRelationshipType(String relationshipType) { this.relationshipType = relationshipType; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class UserScenario {
        private String name;
        private String description;
        private List<String> steps;
        private List<String> endpoints;
        private String purpose;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getSteps() { return steps; }
        public void setSteps(List<String> steps) { this.steps = steps; }
        
        public List<String> getEndpoints() { return endpoints; }
        public void setEndpoints(List<String> endpoints) { this.endpoints = endpoints; }
        
        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
    }
    
    public static class WorkflowPattern {
        private String name;
        private String patternType;
        private List<String> endpoints;
        private String description;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getPatternType() { return patternType; }
        public void setPatternType(String patternType) { this.patternType = patternType; }
        
        public List<String> getEndpoints() { return endpoints; }
        public void setEndpoints(List<String> endpoints) { this.endpoints = endpoints; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class DataTemplate {
        private String name;
        private String type;
        private Map<String, Object> template;
        private String description;
        private List<String> applicableEndpoints;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public Map<String, Object> getTemplate() { return template; }
        public void setTemplate(Map<String, Object> template) { this.template = template; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getApplicableEndpoints() { return applicableEndpoints; }
        public void setApplicableEndpoints(List<String> applicableEndpoints) { this.applicableEndpoints = applicableEndpoints; }
    }
    
    public static class GenerationRule {
        private String name;
        private String fieldName;
        private String ruleType;
        private Map<String, Object> parameters;
        private String description;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }
        
        public String getRuleType() { return ruleType; }
        public void setRuleType(String ruleType) { this.ruleType = ruleType; }
        
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class AnalysisMetrics {
        private int totalSchemas;
        private int totalEndpoints;
        private int totalValidationRules;
        private int totalBusinessRules;
        private int complexityScore;
        private double coverageScore;
        
        // Getters and Setters
        public int getTotalSchemas() { return totalSchemas; }
        public void setTotalSchemas(int totalSchemas) { this.totalSchemas = totalSchemas; }
        
        public int getTotalEndpoints() { return totalEndpoints; }
        public void setTotalEndpoints(int totalEndpoints) { this.totalEndpoints = totalEndpoints; }
        
        public int getTotalValidationRules() { return totalValidationRules; }
        public void setTotalValidationRules(int totalValidationRules) { this.totalValidationRules = totalValidationRules; }
        
        public int getTotalBusinessRules() { return totalBusinessRules; }
        public void setTotalBusinessRules(int totalBusinessRules) { this.totalBusinessRules = totalBusinessRules; }
        
        public int getComplexityScore() { return complexityScore; }
        public void setComplexityScore(int complexityScore) { this.complexityScore = complexityScore; }
        
        public double getCoverageScore() { return coverageScore; }
        public void setCoverageScore(double coverageScore) { this.coverageScore = coverageScore; }
    }
}