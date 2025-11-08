package org.example.domain.dto.openapi;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for API issues/vulnerabilities found during OpenAPI analysis
 */
public class ApiIssueResponse {
    
    private Long id;
    private String analysisId;
    private String endpointId;
    private String type; // Issue type (SECURITY_VULNERABILITY, VALIDATION_ERROR, etc.)
    private String severity; // Severity level (CRITICAL, HIGH, MEDIUM, LOW, INFO)
    private String title;
    private String description;
    private String recommendation;
    private String elementPath;
    private String elementType;
    private Integer lineNumber;
    private String schemaReference;
    private String parameterName;
    private String responseCode;
    private String detailedAnalysis;
    private String fixExample;
    private Boolean isResolved;
    private Boolean isFalsePositive;
    private List<String> tags;
    private LocalDateTime detectedAt;
    private LocalDateTime resolvedAt;
    private Double riskScore;
    private String owaspCategory;
    private Double cvssScore;
    private Boolean isActive;
    
    // Constructors
    public ApiIssueResponse() {}
    
    // Static factory methods for mock data
    public static ApiIssueResponse createMockSecurityIssue(String analysisId) {
        ApiIssueResponse issue = new ApiIssueResponse();
        issue.setAnalysisId(analysisId);
        issue.setType("SECURITY_VULNERABILITY");
        issue.setSeverity("HIGH");
        issue.setTitle("Missing Authentication");
        issue.setDescription("The endpoint does not require authentication for sensitive operations");
        issue.setRecommendation("Add proper authentication requirements to protect sensitive operations");
        issue.setElementPath("/paths/users/{id}/delete");
        issue.setElementType("operation");
        issue.setRiskScore(8.5);
        issue.setOwaspCategory("A07:2021 - Identification and Authentication Failures");
        issue.setCvssScore(7.2);
        issue.setDetectedAt(LocalDateTime.now());
        issue.setIsResolved(false);
        issue.setIsFalsePositive(false);
        issue.setIsActive(true);
        return issue;
    }
    
    public static ApiIssueResponse createMockValidationIssue(String analysisId) {
        ApiIssueResponse issue = new ApiIssueResponse();
        issue.setAnalysisId(analysisId);
        issue.setType("VALIDATION_ERROR");
        issue.setSeverity("MEDIUM");
        issue.setTitle("Missing Input Validation");
        issue.setDescription("The email parameter lacks proper validation constraints");
        issue.setRecommendation("Add regex pattern and length constraints for email parameter");
        issue.setElementPath("/paths/users/post/requestBody/content/application/json/schema/properties/email");
        issue.setElementType("schema");
        issue.setParameterName("email");
        issue.setDetectedAt(LocalDateTime.now());
        issue.setIsResolved(false);
        issue.setIsFalsePositive(false);
        issue.setIsActive(true);
        return issue;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public String getEndpointId() { return endpointId; }
    public void setEndpointId(String endpointId) { this.endpointId = endpointId; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    
    public String getElementPath() { return elementPath; }
    public void setElementPath(String elementPath) { this.elementPath = elementPath; }
    
    public String getElementType() { return elementType; }
    public void setElementType(String elementType) { this.elementType = elementType; }
    
    public Integer getLineNumber() { return lineNumber; }
    public void setLineNumber(Integer lineNumber) { this.lineNumber = lineNumber; }
    
    public String getSchemaReference() { return schemaReference; }
    public void setSchemaReference(String schemaReference) { this.schemaReference = schemaReference; }
    
    public String getParameterName() { return parameterName; }
    public void setParameterName(String parameterName) { this.parameterName = parameterName; }
    
    public String getResponseCode() { return responseCode; }
    public void setResponseCode(String responseCode) { this.responseCode = responseCode; }
    
    public String getDetailedAnalysis() { return detailedAnalysis; }
    public void setDetailedAnalysis(String detailedAnalysis) { this.detailedAnalysis = detailedAnalysis; }
    
    public String getFixExample() { return fixExample; }
    public void setFixExample(String fixExample) { this.fixExample = fixExample; }
    
    public Boolean getIsResolved() { return isResolved; }
    public void setIsResolved(Boolean isResolved) { this.isResolved = isResolved; }
    
    public Boolean getIsFalsePositive() { return isFalsePositive; }
    public void setIsFalsePositive(Boolean isFalsePositive) { this.isFalsePositive = isFalsePositive; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }
    
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    
    public Double getRiskScore() { return riskScore; }
    public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }
    
    public String getOwaspCategory() { return owaspCategory; }
    public void setOwaspCategory(String owaspCategory) { this.owaspCategory = owaspCategory; }
    
    public Double getCvssScore() { return cvssScore; }
    public void setCvssScore(Double cvssScore) { this.cvssScore = cvssScore; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    // Helper methods
    public boolean isSecurityRelated() {
        return "SECURITY_VULNERABILITY".equals(type) || 
               "AUTHENTICATION_ISSUE".equals(type) || 
               "AUTHORIZATION_ISSUE".equals(type);
    }
    
    public boolean isCritical() {
        return "CRITICAL".equals(severity);
    }
    
    public boolean isHighSeverity() {
        return "HIGH".equals(severity) || "CRITICAL".equals(severity);
    }
    
    public boolean isResolved() {
        return Boolean.TRUE.equals(isResolved);
    }
    
    public boolean isFalsePositive() {
        return Boolean.TRUE.equals(isFalsePositive);
    }
    
    public String getFullElementPath() {
        if (elementPath != null && lineNumber != null) {
            return elementPath + ":" + lineNumber;
        }
        return elementPath;
    }
    
    public void addTag(String tag) {
        if (tags == null) {
            tags = new java.util.ArrayList<>();
        }
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    @Override
    public String toString() {
        return "ApiIssueResponse{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", severity='" + severity + '\'' +
                ", title='" + title + '\'' +
                ", isResolved=" + isResolved +
                ", isFalsePositive=" + isFalsePositive +
                ", detectedAt=" + detectedAt +
                '}';
    }
}