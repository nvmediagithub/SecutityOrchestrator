package org.example.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.domain.valueobjects.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing security check results for OpenAPI endpoints
 */
@Entity
@Table(name = "api_security_checks")
public class ApiSecurityCheck {
    
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
    @Column(name = "check_type", nullable = false)
    private SecurityCheckType checkType;
    
    @NotBlank(message = "Check name is required")
    @Column(name = "check_name", nullable = false)
    private String checkName;
    
    @Column(name = "status", nullable = false)
    private String status; // PASSED, FAILED, WARNING, ERROR
    
    @NotBlank(message = "Description is required")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "check_result", columnDefinition = "TEXT")
    private String checkResult;
    
    @Column(name = "actual_value", columnDefinition = "TEXT")
    private String actualValue;
    
    @Column(name = "expected_value", columnDefinition = "TEXT")
    private String expectedValue;
    
    @Column(name = "severity", columnDefinition = "TEXT")
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    
    @Lob
    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;
    
    @Lob
    @Column(name = "fix_instructions", columnDefinition = "TEXT")
    private String fixInstructions;
    
    @Lob
    @Column(name = "evidence", columnDefinition = "TEXT")
    private String evidence;
    
    @Column(name = "element_path", columnDefinition = "TEXT")
    private String elementPath;
    
    @Column(name = "security_scheme", columnDefinition = "TEXT")
    private String securityScheme;
    
    @Column(name = "is_compliant", nullable = false)
    private Boolean isCompliant = true;
    
    @Column(name = "compliance_standard", columnDefinition = "TEXT")
    private String complianceStandard; // OWASP, NIST, etc.
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @NotNull(message = "Check timestamp is required")
    @Column(name = "checked_at", nullable = false)
    private LocalDateTime checkedAt;
    
    @Column(name = "check_duration_ms")
    private Long checkDurationMs;
    
    @Column(name = "confidence_score")
    private Double confidenceScore;
    
    @ElementCollection
    @CollectionTable(name = "security_check_tags", joinColumns = @JoinColumn(name = "security_check_id"))
    @Column(name = "tag")
    private java.util.List<String> tags = new java.util.ArrayList<>();
    
    // Constructors
    public ApiSecurityCheck() {
        this.checkedAt = LocalDateTime.now();
    }
    
    public ApiSecurityCheck(OpenApiAnalysis analysis, SecurityCheckType checkType, String checkName, String status, String description) {
        this();
        this.analysis = analysis;
        this.checkType = checkType;
        this.checkName = checkName;
        this.status = status;
        this.description = description;
    }
    
    public ApiSecurityCheck(ApiEndpointAnalysis endpoint, SecurityCheckType checkType, String checkName, String status, String description) {
        this(endpoint.getAnalysis(), checkType, checkName, status, description);
        this.endpoint = endpoint;
    }
    
    // Business methods
    public void markAsPassed() {
        this.status = "PASSED";
        this.isCompliant = true;
    }
    
    public void markAsFailed(String reason) {
        this.status = "FAILED";
        this.isCompliant = false;
        this.checkResult = reason;
    }
    
    public void markAsWarning(String warning) {
        this.status = "WARNING";
        this.checkResult = warning;
    }
    
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void removeTag(String tag) {
        tags.remove(tag);
    }
    
    public void setCheckDuration(Long durationMs) {
        this.checkDurationMs = durationMs;
    }
    
    public void setConfidenceScore(Double score) {
        this.confidenceScore = score;
    }
    
    public boolean isSecurityCompliant() {
        return isCompliant && "PASSED".equals(status);
    }
    
    public boolean hasSecurityIssues() {
        return "FAILED".equals(status) || "WARNING".equals(status);
    }
    
    public String getSeverityLevel() {
        return severity != null ? severity : determineSeverityFromStatus();
    }
    
    private String determineSeverityFromStatus() {
        switch (status) {
            case "FAILED":
                return "HIGH";
            case "WARNING":
                return "MEDIUM";
            case "PASSED":
                return "LOW";
            default:
                return "LOW";
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public OpenApiAnalysis getAnalysis() { return analysis; }
    public void setAnalysis(OpenApiAnalysis analysis) { this.analysis = analysis; }
    
    public ApiEndpointAnalysis getEndpoint() { return endpoint; }
    public void setEndpoint(ApiEndpointAnalysis endpoint) { this.endpoint = endpoint; }
    
    public SecurityCheckType getCheckType() { return checkType; }
    public void setCheckType(SecurityCheckType checkType) { this.checkType = checkType; }
    
    public String getCheckName() { return checkName; }
    public void setCheckName(String checkName) { this.checkName = checkName; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCheckResult() { return checkResult; }
    public void setCheckResult(String checkResult) { this.checkResult = checkResult; }
    
    public String getActualValue() { return actualValue; }
    public void setActualValue(String actualValue) { this.actualValue = actualValue; }
    
    public String getExpectedValue() { return expectedValue; }
    public void setExpectedValue(String expectedValue) { this.expectedValue = expectedValue; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    
    public String getFixInstructions() { return fixInstructions; }
    public void setFixInstructions(String fixInstructions) { this.fixInstructions = fixInstructions; }
    
    public String getEvidence() { return evidence; }
    public void setEvidence(String evidence) { this.evidence = evidence; }
    
    public String getElementPath() { return elementPath; }
    public void setElementPath(String elementPath) { this.elementPath = elementPath; }
    
    public String getSecurityScheme() { return securityScheme; }
    public void setSecurityScheme(String securityScheme) { this.securityScheme = securityScheme; }
    
    public Boolean getIsCompliant() { return isCompliant; }
    public void setIsCompliant(Boolean isCompliant) { this.isCompliant = isCompliant; }
    
    public String getComplianceStandard() { return complianceStandard; }
    public void setComplianceStandard(String complianceStandard) { this.complianceStandard = complianceStandard; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCheckedAt() { return checkedAt; }
    public void setCheckedAt(LocalDateTime checkedAt) { this.checkedAt = checkedAt; }
    
    public Long getCheckDurationMs() { return checkDurationMs; }
    public void setCheckDurationMs(Long checkDurationMs) { this.checkDurationMs = checkDurationMs; }
    
    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }
    
    public java.util.List<String> getTags() { return tags; }
    public void setTags(java.util.List<String> tags) { this.tags = tags; }
    
    // Mock data for testing
    public static ApiSecurityCheck createMockAuthenticationCheck(OpenApiAnalysis analysis) {
        ApiSecurityCheck check = new ApiSecurityCheck(analysis, SecurityCheckType.AUTHENTICATION_REQUIRED, 
                                                    "Authentication Required", "FAILED", 
                                                    "Endpoint does not require authentication");
        check.setCheckResult("No security requirements found");
        check.setActualValue("No authentication");
        check.setExpectedValue("Bearer token or API key required");
        check.setRecommendations("Add authentication security requirement to protect the endpoint");
        check.setFixInstructions("Add 'security' property to the operation with appropriate scheme");
        check.setElementPath("/paths/users/{id}/delete");
        check.setComplianceStandard("OWASP API Security");
        return check;
    }
    
    public static ApiSecurityCheck createMockHttpsCheck(OpenApiAnalysis analysis) {
        ApiSecurityCheck check = new ApiSecurityCheck(analysis, SecurityCheckType.HTTPS_ENFORCEMENT, 
                                                    "HTTPS Enforcement", "PASSED", 
                                                    "HTTPS is properly configured");
        check.setCheckResult("All endpoints require HTTPS");
        check.setActualValue("HTTPS required");
        check.setExpectedValue("HTTPS required");
        check.setRecommendations("Continue enforcing HTTPS for all endpoints");
        check.setElementPath("/servers");
        check.setComplianceStandard("OWASP API Security");
        return check;
    }
    
    @Override
    public String toString() {
        return "ApiSecurityCheck{" +
                "id=" + id +
                ", checkType=" + checkType +
                ", checkName='" + checkName + '\'' +
                ", status='" + status + '\'' +
                ", isCompliant=" + isCompliant +
                ", checkedAt=" + checkedAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiSecurityCheck that = (ApiSecurityCheck) o;
        return Objects.equals(checkName, that.checkName) &&
               Objects.equals(elementPath, that.elementPath) &&
               Objects.equals(analysis, that.analysis);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(checkName, elementPath, analysis);
    }
}