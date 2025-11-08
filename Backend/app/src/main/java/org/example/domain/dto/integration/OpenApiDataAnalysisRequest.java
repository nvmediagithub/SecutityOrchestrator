package org.example.domain.dto.integration;

import java.util.List;
import java.util.Map;

/**
 * Запрос на анализ данных из OpenAPI спецификации
 */
public class OpenApiDataAnalysisRequest {
    
    private String specificationId;
    private String specificationContent;
    private String format; // json, yaml, auto
    private List<String> analysisTypes; // SCHEMAS, BUSINESS_LOGIC, VALIDATION_RULES, etc.
    private Map<String, Object> analysisOptions;
    private boolean includeExamples;
    private boolean extractRelationships;
    private boolean analyzeSecurity;
    
    public OpenApiDataAnalysisRequest() {}
    
    public OpenApiDataAnalysisRequest(String specificationId, String specificationContent, String format) {
        this.specificationId = specificationId;
        this.specificationContent = specificationContent;
        this.format = format;
    }
    
    // Getters and Setters
    public String getSpecificationId() { return specificationId; }
    public void setSpecificationId(String specificationId) { this.specificationId = specificationId; }
    
    public String getSpecificationContent() { return specificationContent; }
    public void setSpecificationContent(String specificationContent) { this.specificationContent = specificationContent; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    
    public List<String> getAnalysisTypes() { return analysisTypes; }
    public void setAnalysisTypes(List<String> analysisTypes) { this.analysisTypes = analysisTypes; }
    
    public Map<String, Object> getAnalysisOptions() { return analysisOptions; }
    public void setAnalysisOptions(Map<String, Object> analysisOptions) { this.analysisOptions = analysisOptions; }
    
    public boolean isIncludeExamples() { return includeExamples; }
    public void setIncludeExamples(boolean includeExamples) { this.includeExamples = includeExamples; }
    
    public boolean isExtractRelationships() { return extractRelationships; }
    public void setExtractRelationships(boolean extractRelationships) { this.extractRelationships = extractRelationships; }
    
    public boolean isAnalyzeSecurity() { return analyzeSecurity; }
    public void setAnalyzeSecurity(boolean analyzeSecurity) { this.analyzeSecurity = analyzeSecurity; }
}