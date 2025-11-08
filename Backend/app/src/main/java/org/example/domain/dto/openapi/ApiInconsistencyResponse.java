package org.example.domain.dto.openapi;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for API inconsistencies found during OpenAPI analysis
 */
public class ApiInconsistencyResponse {
    
    private Long id;
    private String analysisId;
    private String endpointId;
    private String type; // SCHEMA_INCONSISTENCY, PARAMETER_INCONSISTENCY, etc.
    private String title;
    private String description;
    private String firstElementPath;
    private String firstElementName;
    private String firstElementType;
    private String secondElementPath;
    private String secondElementName;
    private String secondElementType;
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    private String detailedAnalysis;
    private String resolutionSuggestion;
    private String impactDescription;
    private String elementType; // SCHEMA, PARAMETER, RESPONSE, etc.
    private String propertyName;
    private String conflictType; // TYPE_MISMATCH, MISSING_PROPERTY, DUPLICATE_DEFINITION, etc.
    private String expectedValue;
    private String actualValue;
    private String resolutionStatus; // PENDING, RESOLVED, IGNORED
    private Boolean isActive;
    private LocalDateTime detectedAt;
    private LocalDateTime resolvedAt;
    private List<String> tags;
    private Integer lineNumber1;
    private Integer lineNumber2;
    private Double confidenceScore;
    
    // Constructors
    public ApiInconsistencyResponse() {}
    
    // Static factory methods for mock data
    public static ApiInconsistencyResponse createMockSchemaInconsistency(String analysisId) {
        ApiInconsistencyResponse inconsistency = new ApiInconsistencyResponse();
        inconsistency.setAnalysisId(analysisId);
        inconsistency.setType("SCHEMA_INCONSISTENCY");
        inconsistency.setTitle("Schema Type Mismatch");
        inconsistency.setDescription("User schema defines email as string but response schema expects it as object");
        inconsistency.setFirstElementPath("/components/schemas/User/properties/email");
        inconsistency.setFirstElementName("email");
        inconsistency.setFirstElementType("string");
        inconsistency.setSecondElementPath("/components/schemas/UserResponse/properties/email");
        inconsistency.setSecondElementName("email");
        inconsistency.setSecondElementType("object");
        inconsistency.setSeverity("HIGH");
        inconsistency.setConflictType("TYPE_MISMATCH");
        inconsistency.setExpectedValue("string");
        inconsistency.setActualValue("object");
        inconsistency.setResolutionSuggestion("Standardize email field type across all schemas to string");
        inconsistency.setElementType("SCHEMA");
        inconsistency.setResolutionStatus("PENDING");
        inconsistency.setDetectedAt(LocalDateTime.now());
        inconsistency.setIsActive(true);
        return inconsistency;
    }
    
    public static ApiInconsistencyResponse createMockParameterInconsistency(String analysisId) {
        ApiInconsistencyResponse inconsistency = new ApiInconsistencyResponse();
        inconsistency.setAnalysisId(analysisId);
        inconsistency.setType("PARAMETER_INCONSISTENCY");
        inconsistency.setTitle("Parameter Required Mismatch");
        inconsistency.setDescription("Parameter 'id' is required in one endpoint but optional in another");
        inconsistency.setFirstElementPath("/paths/users/{id}/get/parameters");
        inconsistency.setFirstElementName("id");
        inconsistency.setFirstElementType("path");
        inconsistency.setSecondElementPath("/paths/users/{id}/patch/parameters");
        inconsistency.setSecondElementName("id");
        inconsistency.setSecondElementType("path");
        inconsistency.setConflictType("REQUIRED_MISMATCH");
        inconsistency.setExpectedValue("required: true");
        inconsistency.setActualValue("required: false");
        inconsistency.setResolutionSuggestion("Make parameter consistency across all endpoints");
        inconsistency.setElementType("PARAMETER");
        inconsistency.setResolutionStatus("PENDING");
        inconsistency.setDetectedAt(LocalDateTime.now());
        inconsistency.setIsActive(true);
        return inconsistency;
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
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getFirstElementPath() { return firstElementPath; }
    public void setFirstElementPath(String firstElementPath) { this.firstElementPath = firstElementPath; }
    
    public String getFirstElementName() { return firstElementName; }
    public void setFirstElementName(String firstElementName) { this.firstElementName = firstElementName; }
    
    public String getFirstElementType() { return firstElementType; }
    public void setFirstElementType(String firstElementType) { this.firstElementType = firstElementType; }
    
    public String getSecondElementPath() { return secondElementPath; }
    public void setSecondElementPath(String secondElementPath) { this.secondElementPath = secondElementPath; }
    
    public String getSecondElementName() { return secondElementName; }
    public void setSecondElementName(String secondElementName) { this.secondElementName = secondElementName; }
    
    public String getSecondElementType() { return secondElementType; }
    public void setSecondElementType(String secondElementType) { this.secondElementType = secondElementType; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public String getDetailedAnalysis() { return detailedAnalysis; }
    public void setDetailedAnalysis(String detailedAnalysis) { this.detailedAnalysis = detailedAnalysis; }
    
    public String getResolutionSuggestion() { return resolutionSuggestion; }
    public void setResolutionSuggestion(String resolutionSuggestion) { this.resolutionSuggestion = resolutionSuggestion; }
    
    public String getImpactDescription() { return impactDescription; }
    public void setImpactDescription(String impactDescription) { this.impactDescription = impactDescription; }
    
    public String getElementType() { return elementType; }
    public void setElementType(String elementType) { this.elementType = elementType; }
    
    public String getPropertyName() { return propertyName; }
    public void setPropertyName(String propertyName) { this.propertyName = propertyName; }
    
    public String getConflictType() { return conflictType; }
    public void setConflictType(String conflictType) { this.conflictType = conflictType; }
    
    public String getExpectedValue() { return expectedValue; }
    public void setExpectedValue(String expectedValue) { this.expectedValue = expectedValue; }
    
    public String getActualValue() { return actualValue; }
    public void setActualValue(String actualValue) { this.actualValue = actualValue; }
    
    public String getResolutionStatus() { return resolutionStatus; }
    public void setResolutionStatus(String resolutionStatus) { this.resolutionStatus = resolutionStatus; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }
    
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public Integer getLineNumber1() { return lineNumber1; }
    public void setLineNumber1(Integer lineNumber1) { this.lineNumber1 = lineNumber1; }
    
    public Integer getLineNumber2() { return lineNumber2; }
    public void setLineNumber2(Integer lineNumber2) { this.lineNumber2 = lineNumber2; }
    
    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }
    
    // Helper methods
    public boolean isHighSeverity() {
        return "HIGH".equals(severity) || "CRITICAL".equals(severity);
    }
    
    public boolean isPending() {
        return "PENDING".equals(resolutionStatus);
    }
    
    public boolean isResolved() {
        return "RESOLVED".equals(resolutionStatus);
    }
    
    public boolean isIgnored() {
        return "IGNORED".equals(resolutionStatus);
    }
    
    public String getElementComparison() {
        if (firstElementName != null && secondElementName != null) {
            return firstElementName + " vs " + secondElementName;
        }
        return firstElementPath + " vs " + secondElementPath;
    }
    
    public String getFullSeverity() {
        if (severity != null) return severity;
        return determineSeverityFromType();
    }
    
    private String determineSeverityFromType() {
        switch (type) {
            case "SCHEMA_INCONSISTENCY":
            case "RESPONSE_STATUS_CONFLICT":
                return "HIGH";
            case "PARAMETER_INCONSISTENCY":
            case "DATA_TYPE_INCONSISTENCY":
                return "MEDIUM";
            case "DESCRIPTION_INCONSISTENCY":
            case "EXAMPLE_INCONSISTENCY":
                return "LOW";
            default:
                return "MEDIUM";
        }
    }
    
    public void addTag(String tag) {
        if (tags == null) {
            tags = new java.util.ArrayList<>();
        }
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void markAsResolved() {
        this.resolutionStatus = "RESOLVED";
        this.resolvedAt = LocalDateTime.now();
    }
    
    public void markAsIgnored() {
        this.resolutionStatus = "IGNORED";
    }
    
    @Override
    public String toString() {
        return "ApiInconsistencyResponse{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", severity='" + severity + '\'' +
                ", resolutionStatus='" + resolutionStatus + '\'' +
                ", detectedAt=" + detectedAt +
                '}';
    }
}