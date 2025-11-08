package org.example.domain.dto.openapi;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for API endpoint analysis results
 */
public class ApiEndpointAnalysisResponse {
    
    private Long id;
    private String analysisId;
    private String endpointPath;
    private String httpMethod; // GET, POST, PUT, DELETE, etc.
    private String operationId;
    private String summary;
    private String description;
    private Boolean hasSecurityRequirements;
    private String securitySchemes;
    private Boolean hasParameters;
    private Integer parametersCount;
    private Boolean hasRequestBody;
    private String requestBodySchema;
    private Boolean hasResponseSchemas;
    private String responseSchemas;
    private String responseStatusCodes;
    private Integer totalIssues;
    private Integer securityIssues;
    private Integer validationIssues;
    private Integer consistencyIssues;
    private Integer documentationIssues;
    private Integer performanceIssues;
    private Integer criticalIssues;
    private Double complexityScore;
    private Double securityScore;
    private String analysisDetails;
    private String validationResults;
    private String securityAnalysis;
    private List<String> tags;
    private List<String> issueIds; // References to related issues
    private List<String> securityCheckIds; // References to security checks
    private LocalDateTime analyzedAt;
    private Long analysisDurationMs;
    private Boolean isActive;
    
    // Constructors
    public ApiEndpointAnalysisResponse() {}
    
    // Static factory methods for mock data
    public static ApiEndpointAnalysisResponse createMock(String analysisId) {
        ApiEndpointAnalysisResponse endpoint = new ApiEndpointAnalysisResponse();
        endpoint.setAnalysisId(analysisId);
        endpoint.setEndpointPath("/api/users/{id}");
        endpoint.setHttpMethod("GET");
        endpoint.setOperationId("getUserById");
        endpoint.setSummary("Get user by ID");
        endpoint.setDescription("Retrieves user information by user ID");
        endpoint.setHasSecurityRequirements(true);
        endpoint.setHasParameters(true);
        endpoint.setParametersCount(2);
        endpoint.setHasRequestBody(false);
        endpoint.setHasResponseSchemas(true);
        endpoint.setResponseStatusCodes("200,404,500");
        endpoint.setTotalIssues(1);
        endpoint.setSecurityIssues(1);
        endpoint.setCriticalIssues(0);
        endpoint.setSecurityScore(85.5);
        endpoint.setComplexityScore(75.0);
        endpoint.setAnalysisDetails("Mock endpoint analysis details");
        endpoint.setAnalyzedAt(LocalDateTime.now());
        endpoint.setAnalysisDurationMs(250L);
        endpoint.setIsActive(true);
        return endpoint;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public String getEndpointPath() { return endpointPath; }
    public void setEndpointPath(String endpointPath) { this.endpointPath = endpointPath; }
    
    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
    
    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getHasSecurityRequirements() { return hasSecurityRequirements; }
    public void setHasSecurityRequirements(Boolean hasSecurityRequirements) { this.hasSecurityRequirements = hasSecurityRequirements; }
    
    public String getSecuritySchemes() { return securitySchemes; }
    public void setSecuritySchemes(String securitySchemes) { this.securitySchemes = securitySchemes; }
    
    public Boolean getHasParameters() { return hasParameters; }
    public void setHasParameters(Boolean hasParameters) { this.hasParameters = hasParameters; }
    
    public Integer getParametersCount() { return parametersCount; }
    public void setParametersCount(Integer parametersCount) { this.parametersCount = parametersCount; }
    
    public Boolean getHasRequestBody() { return hasRequestBody; }
    public void setHasRequestBody(Boolean hasRequestBody) { this.hasRequestBody = hasRequestBody; }
    
    public String getRequestBodySchema() { return requestBodySchema; }
    public void setRequestBodySchema(String requestBodySchema) { this.requestBodySchema = requestBodySchema; }
    
    public Boolean getHasResponseSchemas() { return hasResponseSchemas; }
    public void setHasResponseSchemas(Boolean hasResponseSchemas) { this.hasResponseSchemas = hasResponseSchemas; }
    
    public String getResponseSchemas() { return responseSchemas; }
    public void setResponseSchemas(String responseSchemas) { this.responseSchemas = responseSchemas; }
    
    public String getResponseStatusCodes() { return responseStatusCodes; }
    public void setResponseStatusCodes(String responseStatusCodes) { this.responseStatusCodes = responseStatusCodes; }
    
    public Integer getTotalIssues() { return totalIssues; }
    public void setTotalIssues(Integer totalIssues) { this.totalIssues = totalIssues; }
    
    public Integer getSecurityIssues() { return securityIssues; }
    public void setSecurityIssues(Integer securityIssues) { this.securityIssues = securityIssues; }
    
    public Integer getValidationIssues() { return validationIssues; }
    public void setValidationIssues(Integer validationIssues) { this.validationIssues = validationIssues; }
    
    public Integer getConsistencyIssues() { return consistencyIssues; }
    public void setConsistencyIssues(Integer consistencyIssues) { this.consistencyIssues = consistencyIssues; }
    
    public Integer getDocumentationIssues() { return documentationIssues; }
    public void setDocumentationIssues(Integer documentationIssues) { this.documentationIssues = documentationIssues; }
    
    public Integer getPerformanceIssues() { return performanceIssues; }
    public void setPerformanceIssues(Integer performanceIssues) { this.performanceIssues = performanceIssues; }
    
    public Integer getCriticalIssues() { return criticalIssues; }
    public void setCriticalIssues(Integer criticalIssues) { this.criticalIssues = criticalIssues; }
    
    public Double getComplexityScore() { return complexityScore; }
    public void setComplexityScore(Double complexityScore) { this.complexityScore = complexityScore; }
    
    public Double getSecurityScore() { return securityScore; }
    public void setSecurityScore(Double securityScore) { this.securityScore = securityScore; }
    
    public String getAnalysisDetails() { return analysisDetails; }
    public void setAnalysisDetails(String analysisDetails) { this.analysisDetails = analysisDetails; }
    
    public String getValidationResults() { return validationResults; }
    public void setValidationResults(String validationResults) { this.validationResults = validationResults; }
    
    public String getSecurityAnalysis() { return securityAnalysis; }
    public void setSecurityAnalysis(String securityAnalysis) { this.securityAnalysis = securityAnalysis; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public List<String> getIssueIds() { return issueIds; }
    public void setIssueIds(List<String> issueIds) { this.issueIds = issueIds; }
    
    public List<String> getSecurityCheckIds() { return securityCheckIds; }
    public void setSecurityCheckIds(List<String> securityCheckIds) { this.securityCheckIds = securityCheckIds; }
    
    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
    
    public Long getAnalysisDurationMs() { return analysisDurationMs; }
    public void setAnalysisDurationMs(Long analysisDurationMs) { this.analysisDurationMs = analysisDurationMs; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    // Helper methods
    public double getOverallScore() {
        if (complexityScore != null && securityScore != null) {
            return (complexityScore + securityScore) / 2.0;
        }
        return securityScore != null ? securityScore : complexityScore != null ? complexityScore : 0.0;
    }
    
    public boolean hasCriticalIssues() {
        return criticalIssues != null && criticalIssues > 0;
    }
    
    public boolean hasHighSecurityIssues() {
        return securityIssues != null && securityIssues > 0;
    }
    
    public boolean hasSecurityRequirements() {
        return Boolean.TRUE.equals(hasSecurityRequirements);
    }
    
    public boolean hasParameters() {
        return Boolean.TRUE.equals(hasParameters);
    }
    
    public boolean hasRequestBody() {
        return Boolean.TRUE.equals(hasRequestBody);
    }
    
    public boolean hasResponseSchemas() {
        return Boolean.TRUE.equals(hasResponseSchemas);
    }
    
    public void addTag(String tag) {
        if (tags == null) {
            tags = new java.util.ArrayList<>();
        }
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void addIssueId(String issueId) {
        if (issueIds == null) {
            issueIds = new java.util.ArrayList<>();
        }
        if (!issueIds.contains(issueId)) {
            issueIds.add(issueId);
        }
    }
    
    public void addSecurityCheckId(String securityCheckId) {
        if (securityCheckIds == null) {
            securityCheckIds = new java.util.ArrayList<>();
        }
        if (!securityCheckIds.contains(securityCheckId)) {
            securityCheckIds.add(securityCheckId);
        }
    }
    
    @Override
    public String toString() {
        return "ApiEndpointAnalysisResponse{" +
                "id=" + id +
                ", endpointPath='" + endpointPath + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", totalIssues=" + totalIssues +
                ", securityScore=" + securityScore +
                ", overallScore=" + getOverallScore() +
                ", analyzedAt=" + analyzedAt +
                '}';
    }
}