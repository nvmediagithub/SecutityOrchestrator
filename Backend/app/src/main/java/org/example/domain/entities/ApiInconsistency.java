package org.example.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.domain.valueobjects.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing inconsistencies found during OpenAPI analysis
 */
@Entity
@Table(name = "api_inconsistencies")
public class ApiInconsistency {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    private OpenApiAnalysis analysis;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endpoint_id")
    private ApiEndpointAnalysis endpoint;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "inconsistency_type", nullable = false)
    private InconsistencyType type;
    
    @NotBlank(message = "Title is required")
    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;
    
    @NotBlank(message = "Description is required")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "first_element_path", columnDefinition = "TEXT")
    private String firstElementPath;
    
    @Column(name = "first_element_name", columnDefinition = "TEXT")
    private String firstElementName;
    
    @Column(name = "first_element_type", columnDefinition = "TEXT")
    private String firstElementType;
    
    @Column(name = "second_element_path", columnDefinition = "TEXT")
    private String secondElementPath;
    
    @Column(name = "second_element_name", columnDefinition = "TEXT")
    private String secondElementName;
    
    @Column(name = "second_element_type", columnDefinition = "TEXT")
    private String secondElementType;
    
    @Column(name = "severity", columnDefinition = "TEXT")
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    
    @Lob
    @Column(name = "detailed_analysis", columnDefinition = "TEXT")
    private String detailedAnalysis;
    
    @Lob
    @Column(name = "resolution_suggestion", columnDefinition = "TEXT")
    private String resolutionSuggestion;
    
    @Lob
    @Column(name = "impact_description", columnDefinition = "TEXT")
    private String impactDescription;
    
    @Column(name = "element_type", columnDefinition = "TEXT")
    private String elementType; // SCHEMA, PARAMETER, RESPONSE, etc.
    
    @Column(name = "property_name", columnDefinition = "TEXT")
    private String propertyName;
    
    @Column(name = "conflict_type", columnDefinition = "TEXT")
    private String conflictType; // TYPE_MISMATCH, MISSING_PROPERTY, DUPLICATE_DEFINITION, etc.
    
    @Column(name = "expected_value", columnDefinition = "TEXT")
    private String expectedValue;
    
    @Column(name = "actual_value", columnDefinition = "TEXT")
    private String actualValue;
    
    @Column(name = "resolution_status", columnDefinition = "TEXT")
    private String resolutionStatus; // PENDING, RESOLVED, IGNORED
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @NotNull(message = "Detection timestamp is required")
    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @ElementCollection
    @CollectionTable(name = "inconsistency_tags", joinColumns = @JoinColumn(name = "inconsistency_id"))
    @Column(name = "tag")
    private java.util.List<String> tags = new java.util.ArrayList<>();
    
    @Column(name = "line_number_1")
    private Integer lineNumber1;
    
    @Column(name = "line_number_2")
    private Integer lineNumber2;
    
    @Column(name = "confidence_score")
    private Double confidenceScore;
    
    // Constructors
    public ApiInconsistency() {
        this.detectedAt = LocalDateTime.now();
    }
    
    public ApiInconsistency(OpenApiAnalysis analysis, InconsistencyType type, String title, String description) {
        this();
        this.analysis = analysis;
        this.type = type;
        this.title = title;
        this.description = description;
        this.resolutionStatus = "PENDING";
    }
    
    public ApiInconsistency(ApiEndpointAnalysis endpoint, InconsistencyType type, String title, String description) {
        this(endpoint.getAnalysis(), type, title, description);
        this.endpoint = endpoint;
    }
    
    // Business methods
    public void markAsResolved() {
        this.resolutionStatus = "RESOLVED";
        this.resolvedAt = LocalDateTime.now();
    }
    
    public void markAsIgnored() {
        this.resolutionStatus = "IGNORED";
    }
    
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void removeTag(String tag) {
        tags.remove(tag);
    }
    
    public boolean isHighSeverity() {
        return "HIGH".equals(severity) || "CRITICAL".equals(severity);
    }
    
    public boolean isPending() {
        return "PENDING".equals(resolutionStatus);
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
            case SCHEMA_INCONSISTENCY:
            case RESPONSE_STATUS_CONFLICT:
                return "HIGH";
            case PARAMETER_INCONSISTENCY:
            case DATA_TYPE_INCONSISTENCY:
                return "MEDIUM";
            case DESCRIPTION_INCONSISTENCY:
            case EXAMPLE_INCONSISTENCY:
                return "LOW";
            default:
                return "MEDIUM";
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public OpenApiAnalysis getAnalysis() { return analysis; }
    public void setAnalysis(OpenApiAnalysis analysis) { this.analysis = analysis; }
    
    public ApiEndpointAnalysis getEndpoint() { return endpoint; }
    public void setEndpoint(ApiEndpointAnalysis endpoint) { this.endpoint = endpoint; }
    
    public InconsistencyType getType() { return type; }
    public void setType(InconsistencyType type) { this.type = type; }
    
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
    
    public java.util.List<String> getTags() { return tags; }
    public void setTags(java.util.List<String> tags) { this.tags = tags; }
    
    public Integer getLineNumber1() { return lineNumber1; }
    public void setLineNumber1(Integer lineNumber1) { this.lineNumber1 = lineNumber1; }
    
    public Integer getLineNumber2() { return lineNumber2; }
    public void setLineNumber2(Integer lineNumber2) { this.lineNumber2 = lineNumber2; }
    
    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }
    
    // Mock data for testing
    public static ApiInconsistency createMockSchemaInconsistency(OpenApiAnalysis analysis) {
        ApiInconsistency inconsistency = new ApiInconsistency(analysis, InconsistencyType.SCHEMA_INCONSISTENCY,
                                                             "Schema Type Mismatch",
                                                             "User schema defines email as string but response schema expects it as object");
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
        return inconsistency;
    }
    
    public static ApiInconsistency createMockParameterInconsistency(OpenApiAnalysis analysis) {
        ApiInconsistency inconsistency = new ApiInconsistency(analysis, InconsistencyType.PARAMETER_INCONSISTENCY,
                                                             "Parameter Required Mismatch",
                                                             "Parameter 'id' is required in one endpoint but optional in another");
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
        return inconsistency;
    }
    
    @Override
    public String toString() {
        return "ApiInconsistency{" +
                "id=" + id +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", severity='" + severity + '\'' +
                ", resolutionStatus='" + resolutionStatus + '\'' +
                ", detectedAt=" + detectedAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiInconsistency that = (ApiInconsistency) o;
        return Objects.equals(analysis, that.analysis) &&
               Objects.equals(firstElementPath, that.firstElementPath) &&
               Objects.equals(secondElementPath, that.secondElementPath) &&
               Objects.equals(title, that.title);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(analysis, firstElementPath, secondElementPath, title);
    }
}