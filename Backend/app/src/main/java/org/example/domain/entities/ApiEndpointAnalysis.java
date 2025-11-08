package org.example.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.domain.valueobjects.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing analysis results for individual API endpoints
 */
@Entity
@Table(name = "api_endpoint_analyses")
public class ApiEndpointAnalysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "analysis_id", nullable = false)
    private String analysisId; // Temporary: stores analysis ID instead of reference
    
    @NotBlank(message = "Endpoint path is required")
    @Column(name = "endpoint_path", nullable = false)
    private String endpointPath;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "http_method", nullable = false)
    private HttpMethod httpMethod;
    
    @Column(name = "operation_id")
    private String operationId;
    
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "has_security_requirements", nullable = false)
    private Boolean hasSecurityRequirements = false;
    
    @Column(name = "security_schemes", columnDefinition = "TEXT")
    private String securitySchemes;
    
    @Column(name = "has_parameters", nullable = false)
    private Boolean hasParameters = false;
    
    @Column(name = "parameters_count", nullable = false)
    private Integer parametersCount = 0;
    
    @Column(name = "has_request_body", nullable = false)
    private Boolean hasRequestBody = false;
    
    @Column(name = "request_body_schema")
    private String requestBodySchema;
    
    @Column(name = "has_response_schemas", nullable = false)
    private Boolean hasResponseSchemas = false;
    
    @Column(name = "response_schemas", columnDefinition = "TEXT")
    private String responseSchemas;
    
    @Column(name = "response_status_codes", columnDefinition = "TEXT")
    private String responseStatusCodes;
    
    @Column(name = "total_issues", nullable = false)
    private Integer totalIssues = 0;
    
    @Column(name = "security_issues", nullable = false)
    private Integer securityIssues = 0;
    
    @Column(name = "validation_issues", nullable = false)
    private Integer validationIssues = 0;
    
    @Column(name = "consistency_issues", nullable = false)
    private Integer consistencyIssues = 0;
    
    @Column(name = "documentation_issues", nullable = false)
    private Integer documentationIssues = 0;
    
    @Column(name = "performance_issues", nullable = false)
    private Integer performanceIssues = 0;
    
    @Column(name = "critical_issues", nullable = false)
    private Integer criticalIssues = 0;
    
    @Column(name = "complexity_score")
    private Double complexityScore;
    
    @Column(name = "security_score")
    private Double securityScore;
    
    @Lob
    @Column(name = "analysis_details", columnDefinition = "TEXT")
    private String analysisDetails;
    
    @Lob
    @Column(name = "validation_results", columnDefinition = "TEXT")
    private String validationResults;
    
    @Lob
    @Column(name = "security_analysis", columnDefinition = "TEXT")
    private String securityAnalysis;
    
    @ElementCollection
    @CollectionTable(name = "endpoint_analysis_tags", joinColumns = @JoinColumn(name = "endpoint_analysis_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
    
    // @OneToMany(mappedBy = "endpoint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<String> issueIds = new java.util.ArrayList<>(); // Temporary: stores issue IDs
    
    // @OneToMany(mappedBy = "endpoint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<String> securityCheckIds = new java.util.ArrayList<>(); // Temporary: stores security check IDs
    
    @NotNull(message = "Creation timestamp is required")
    @Column(name = "analyzed_at", nullable = false)
    private LocalDateTime analyzedAt;
    
    @Column(name = "analysis_duration_ms")
    private Long analysisDurationMs;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    // Constructors
    public ApiEndpointAnalysis() {
        this.analyzedAt = LocalDateTime.now();
    }
    
    public ApiEndpointAnalysis(String analysisId, String endpointPath, HttpMethod httpMethod) {
        this();
        this.analysisId = analysisId;
        this.endpointPath = endpointPath;
        this.httpMethod = httpMethod;
    }
    
    // Business methods
    public void markAsAnalyzed() {
        this.analyzedAt = LocalDateTime.now();
    }
    
    public void addIssue() {
        this.totalIssues++;
        recalculateIssueCategories();
    }
    
    private void recalculateIssueCategories() {
        // TODO: Implement when Issue entity is properly integrated
        // this.securityIssues = (int) issues.stream().filter(i -> i.getType() == IssueType.SECURITY_VULNERABILITY).count();
        // this.criticalIssues = (int) issues.stream().filter(i -> i.getSeverity() == SeverityLevel.CRITICAL).count();
        // Temporary logic based on counters
        this.securityIssues = Math.max(0, this.totalIssues / 4);
        this.criticalIssues = Math.max(0, this.totalIssues / 8);
        this.validationIssues = Math.max(0, this.totalIssues / 4);
        this.consistencyIssues = Math.max(0, this.totalIssues / 4);
        this.documentationIssues = Math.max(0, this.totalIssues / 8);
        this.performanceIssues = Math.max(0, this.totalIssues / 8);
    }
    
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void removeTag(String tag) {
        tags.remove(tag);
    }
    
    public void setAnalysisDuration(Long durationMs) {
        this.analysisDurationMs = durationMs;
    }
    
    public double getOverallScore() {
        if (complexityScore != null && securityScore != null) {
            return (complexityScore + securityScore) / 2.0;
        }
        return securityScore != null ? securityScore : complexityScore != null ? complexityScore : 0.0;
    }
    
    public boolean hasCriticalIssues() {
        // TODO: Implement when Issue entity is properly integrated
        return this.criticalIssues > 0;
    }
    
    public boolean hasHighSecurityIssues() {
        // TODO: Implement when Issue entity is properly integrated
        return this.securityIssues > 0;
    }
    
    public Integer getCriticalIssues() { return criticalIssues; }
    public void setCriticalIssues(Integer criticalIssues) { this.criticalIssues = criticalIssues; }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public String getEndpointPath() { return endpointPath; }
    public void setEndpointPath(String endpointPath) { this.endpointPath = endpointPath; }
    
    public HttpMethod getHttpMethod() { return httpMethod; }
    public void setHttpMethod(HttpMethod httpMethod) { this.httpMethod = httpMethod; }
    
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
    
    public java.util.List<String> getIssueIds() { return issueIds; }
    public void setIssueIds(java.util.List<String> issueIds) { this.issueIds = issueIds; }
    
    public java.util.List<String> getSecurityCheckIds() { return securityCheckIds; }
    public void setSecurityCheckIds(java.util.List<String> securityCheckIds) { this.securityCheckIds = securityCheckIds; }
    
    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
    
    public Long getAnalysisDurationMs() { return analysisDurationMs; }
    public void setAnalysisDurationMs(Long analysisDurationMs) { this.analysisDurationMs = analysisDurationMs; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    // Mock data for testing
    public static ApiEndpointAnalysis createMockAnalysis(String analysisId) {
        ApiEndpointAnalysis endpoint = new ApiEndpointAnalysis(analysisId, "/api/users/{id}", HttpMethod.GET);
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
        endpoint.setSecurityScore(85.5);
        endpoint.setComplexityScore(75.0);
        endpoint.setAnalysisDetails("Mock endpoint analysis details");
        return endpoint;
    }
    
    @Override
    public String toString() {
        return "ApiEndpointAnalysis{" +
                "id=" + id +
                ", endpointPath='" + endpointPath + '\'' +
                ", httpMethod=" + httpMethod +
                ", totalIssues=" + totalIssues +
                ", securityScore=" + securityScore +
                ", analyzedAt=" + analyzedAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiEndpointAnalysis that = (ApiEndpointAnalysis) o;
        return Objects.equals(endpointPath, that.endpointPath) && 
               Objects.equals(httpMethod, that.httpMethod) &&
               Objects.equals(analysisId, that.analysisId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(endpointPath, httpMethod, analysisId);
    }
}