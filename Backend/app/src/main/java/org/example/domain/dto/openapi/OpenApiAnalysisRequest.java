package org.example.domain.dto.openapi;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO for OpenAPI analysis request containing parameters for starting an analysis
 */
public class OpenApiAnalysisRequest {
    
    @NotBlank(message = "Specification ID is required")
    private String specificationId;
    
    @NotBlank(message = "Specification title is required")
    private String specificationTitle;
    
    private String specificationVersion;
    
    private String specificationContent; // OpenAPI specification content (YAML/JSON)
    
    private String analyzerVersion;
    
    private List<String> analysisOptions; // List of analysis options to enable
    
    private List<String> tags; // Tags to associate with the analysis
    
    private Boolean includeSecurityChecks = true;
    
    private Boolean includeValidationChecks = true;
    
    private Boolean includeConsistencyChecks = true;
    
    private Boolean includePerformanceChecks = false;
    
    private Boolean generateReport = true;
    
    private String baseUrl; // Base URL for API testing
    
    private Integer timeoutSeconds = 300;
    
    // Constructors
    public OpenApiAnalysisRequest() {}
    
    public OpenApiAnalysisRequest(String specificationId, String specificationTitle) {
        this.specificationId = specificationId;
        this.specificationTitle = specificationTitle;
    }
    
    // Static factory methods
    public static OpenApiAnalysisRequest createBasic(String specificationId, String specificationTitle) {
        return new OpenApiAnalysisRequest(specificationId, specificationTitle);
    }
    
    public static OpenApiAnalysisRequest createFull(String specificationId, String specificationTitle, 
                                                   String specificationContent) {
        OpenApiAnalysisRequest request = new OpenApiAnalysisRequest(specificationId, specificationTitle);
        request.setSpecificationContent(specificationContent);
        return request;
    }
    
    public static OpenApiAnalysisRequest createWithOptions(String specificationId, String specificationTitle,
                                                          List<String> options) {
        OpenApiAnalysisRequest request = new OpenApiAnalysisRequest(specificationId, specificationTitle);
        request.setAnalysisOptions(options);
        return request;
    }
    
    // Getters and Setters
    public String getSpecificationId() { return specificationId; }
    public void setSpecificationId(String specificationId) { this.specificationId = specificationId; }
    
    public String getSpecificationTitle() { return specificationTitle; }
    public void setSpecificationTitle(String specificationTitle) { this.specificationTitle = specificationTitle; }
    
    public String getSpecificationVersion() { return specificationVersion; }
    public void setSpecificationVersion(String specificationVersion) { this.specificationVersion = specificationVersion; }
    
    public String getSpecificationContent() { return specificationContent; }
    public void setSpecificationContent(String specificationContent) { this.specificationContent = specificationContent; }
    
    public String getAnalyzerVersion() { return analyzerVersion; }
    public void setAnalyzerVersion(String analyzerVersion) { this.analyzerVersion = analyzerVersion; }
    
    public List<String> getAnalysisOptions() { return analysisOptions; }
    public void setAnalysisOptions(List<String> analysisOptions) { this.analysisOptions = analysisOptions; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public Boolean getIncludeSecurityChecks() { return includeSecurityChecks; }
    public void setIncludeSecurityChecks(Boolean includeSecurityChecks) { this.includeSecurityChecks = includeSecurityChecks; }
    
    public Boolean getIncludeValidationChecks() { return includeValidationChecks; }
    public void setIncludeValidationChecks(Boolean includeValidationChecks) { this.includeValidationChecks = includeValidationChecks; }
    
    public Boolean getIncludeConsistencyChecks() { return includeConsistencyChecks; }
    public void setIncludeConsistencyChecks(Boolean includeConsistencyChecks) { this.includeConsistencyChecks = includeConsistencyChecks; }
    
    public Boolean getIncludePerformanceChecks() { return includePerformanceChecks; }
    public void setIncludePerformanceChecks(Boolean includePerformanceChecks) { this.includePerformanceChecks = includePerformanceChecks; }
    
    public Boolean getGenerateReport() { return generateReport; }
    public void setGenerateReport(Boolean generateReport) { this.generateReport = generateReport; }
    
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    
    public Integer getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(Integer timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
    
    // Helper methods
    public boolean hasSpecificationContent() {
        return specificationContent != null && !specificationContent.trim().isEmpty();
    }
    
    public boolean hasAnalysisOptions() {
        return analysisOptions != null && !analysisOptions.isEmpty();
    }
    
    public boolean hasTags() {
        return tags != null && !tags.isEmpty();
    }
    
    public boolean isQuickAnalysis() {
        return Boolean.FALSE.equals(includeSecurityChecks) && 
               Boolean.FALSE.equals(includeValidationChecks) && 
               Boolean.FALSE.equals(includeConsistencyChecks);
    }
    
    public boolean isFullAnalysis() {
        return Boolean.TRUE.equals(includeSecurityChecks) && 
               Boolean.TRUE.equals(includeValidationChecks) && 
               Boolean.TRUE.equals(includeConsistencyChecks);
    }
    
    public void addTag(String tag) {
        if (tags == null) {
            tags = new java.util.ArrayList<>();
        }
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void addAnalysisOption(String option) {
        if (analysisOptions == null) {
            analysisOptions = new java.util.ArrayList<>();
        }
        if (!analysisOptions.contains(option)) {
            analysisOptions.add(option);
        }
    }
    
    @Override
    public String toString() {
        return "OpenApiAnalysisRequest{" +
                "specificationId='" + specificationId + '\'' +
                ", specificationTitle='" + specificationTitle + '\'' +
                ", specificationVersion='" + specificationVersion + '\'' +
                ", includeSecurityChecks=" + includeSecurityChecks +
                ", includeValidationChecks=" + includeValidationChecks +
                ", includeConsistencyChecks=" + includeConsistencyChecks +
                ", hasContent=" + hasSpecificationContent() +
                '}';
    }
}