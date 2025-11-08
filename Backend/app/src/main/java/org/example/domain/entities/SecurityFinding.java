package org.example.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.domain.valueobjects.OwaspTestCategory;
import org.example.domain.valueobjects.SeverityLevel;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a security finding/vulnerability discovered during test execution
 */
@Entity
@Table(name = "security_findings")
public class SecurityFinding {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Security test is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "security_test_id", nullable = false)
    private SecurityTest securityTest;
    
    @NotBlank(message = "Finding title is required")
    @Column(name = "finding_title", nullable = false)
    private String findingTitle;
    
    @Column(name = "finding_description", columnDefinition = "TEXT")
    private String findingDescription;
    
    @NotNull(message = "OWASP category is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "owasp_category", nullable = false)
    private OwaspTestCategory owaspCategory;
    
    @NotNull(message = "Severity level is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "severity_level", nullable = false)
    private SeverityLevel severityLevel;
    
    @Column(name = "status", nullable = false)
    private String status; // NEW, CONFIRMED, FALSE_POSITIVE, RESOLVED, ACCEPTED_RISK
    
    @Column(name = "finding_type", nullable = false)
    private String findingType; // VULNERABILITY, WEAKNESS, INFO, COMPLIANCE_ISSUE
    
    @Column(name = "cwe_id")
    private String cweId; // Common Weakness Enumeration ID
    
    @Column(name = "cvss_score", precision = 3, scale = 1)
    private Double cvssScore; // Common Vulnerability Scoring System score 0.0-10.0
    
    @Column(name = "cvss_vector", columnDefinition = "TEXT")
    private String cvssVector; // CVSS vector string
    
    @Lob
    @Column(name = "evidence", columnDefinition = "TEXT")
    private String evidence; // Proof of concept, screenshots, request/response data
    
    @Lob
    @Column(name = "exploit_details", columnDefinition = "TEXT")
    private String exploitDetails; // How to exploit the vulnerability
    
    @Lob
    @Column(name = "impact_description", columnDefinition = "TEXT")
    private String impactDescription; // Business impact of the finding
    
    @Lob
    @Column(name = "recommendation", columnDefinition = "TEXT")
    private String recommendation; // How to fix the issue
    
    @Lob
    @Column(name = "remediation_steps", columnDefinition = "TEXT")
    private String remediationSteps; // Step-by-step fix instructions
    
    @Lob
    @Column(name = "references", columnDefinition = "TEXT")
    private String references; // URLs, documentation, CVE references
    
    @Column(name = "affected_endpoint", columnDefinition = "TEXT")
    private String affectedEndpoint; // The API endpoint affected
    
    @Column(name = "http_method")
    private String httpMethod; // GET, POST, PUT, DELETE, etc.
    
    @Column(name = "parameter_name", columnDefinition = "TEXT")
    private String parameterName; // Which parameter is vulnerable
    
    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload; // The actual payload that triggered the finding
    
    @Column(name = "response_status_code")
    private Integer responseStatusCode; // HTTP response code that indicates vulnerability
    
    @Lob
    @Column(name = "response_data", columnDefinition = "TEXT")
    private String responseData; // Sample response showing the vulnerability
    
    @Column(name = "confidence_score", precision = 3, scale = 2)
    private Double confidenceScore; // How confident we are in this finding 0.00-1.00
    
    @Column(name = "is_false_positive", nullable = false)
    private Boolean isFalsePositive = false;
    
    @Column(name = "requires_manual_verification", nullable = false)
    private Boolean requiresManualVerification = false;
    
    @Column(name = "discovered_at", nullable = false)
    private LocalDateTime discoveredAt;
    
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by", nullable = false)
    private String createdBy;
    
    @Column(name = "last_modified_by")
    private String lastModifiedBy;
    
    @Column(name = "version", nullable = false)
    private Integer version = 1;
    
    // Constructors
    public SecurityFinding() {
        this.createdAt = LocalDateTime.now();
        this.discoveredAt = LocalDateTime.now();
        this.status = "NEW";
    }
    
    public SecurityFinding(SecurityTest securityTest, String findingTitle, OwaspTestCategory owaspCategory, 
                          SeverityLevel severityLevel, String findingType) {
        this();
        this.securityTest = securityTest;
        this.findingTitle = findingTitle;
        this.owaspCategory = owaspCategory;
        this.severityLevel = severityLevel;
        this.findingType = findingType;
    }
    
    // Business methods
    public void markAsConfirmed() {
        this.status = "CONFIRMED";
        this.verifiedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsFalsePositive() {
        this.status = "FALSE_POSITIVE";
        this.isFalsePositive = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsResolved() {
        this.status = "RESOLVED";
        this.resolvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsAcceptedRisk() {
        this.status = "ACCEPTED_RISK";
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isResolved() {
        return "RESOLVED".equals(status);
    }
    
    public boolean isFalsePositive() {
        return "FALSE_POSITIVE".equals(status) || isFalsePositive;
    }
    
    public boolean isConfirmed() {
        return "CONFIRMED".equals(status);
    }
    
    public boolean requiresAttention() {
        return "NEW".equals(status) || "CONFIRMED".equals(status);
    }
    
    public double getRiskScore() {
        if (cvssScore != null) {
            return cvssScore;
        }
        
        // Calculate risk score based on severity if CVSS not available
        switch (severityLevel) {
            case CRITICAL: return 9.0;
            case HIGH: return 7.0;
            case MEDIUM: return 5.0;
            case LOW: return 2.0;
            default: return 0.0;
        }
    }
    
    public String getFormattedReferences() {
        if (references == null || references.trim().isEmpty()) {
            return "";
        }
        
        return references.replace("\n", "<br>").replace("\r", "");
    }
    
    public String getSeverityLabel() {
        switch (severityLevel) {
            case CRITICAL: return "🔴 Critical";
            case HIGH: return "🟠 High";
            case MEDIUM: return "🟡 Medium";
            case LOW: return "🟢 Low";
            default: return "⚪ Unknown";
        }
    }
    
    public String getStatusLabel() {
        switch (status) {
            case "NEW": return "🆕 New";
            case "CONFIRMED": return "✅ Confirmed";
            case "FALSE_POSITIVE": return "❌ False Positive";
            case "RESOLVED": return "🔧 Resolved";
            case "ACCEPTED_RISK": return "⚠️ Accepted Risk";
            default: return status;
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public SecurityTest getSecurityTest() { return securityTest; }
    public void setSecurityTest(SecurityTest securityTest) { this.securityTest = securityTest; }
    
    public String getFindingTitle() { return findingTitle; }
    public void setFindingTitle(String findingTitle) { this.findingTitle = findingTitle; }
    
    public String getFindingDescription() { return findingDescription; }
    public void setFindingDescription(String findingDescription) { this.findingDescription = findingDescription; }
    
    public OwaspTestCategory getOwaspCategory() { return owaspCategory; }
    public void setOwaspCategory(OwaspTestCategory owaspCategory) { this.owaspCategory = owaspCategory; }
    
    public SeverityLevel getSeverityLevel() { return severityLevel; }
    public void setSeverityLevel(SeverityLevel severityLevel) { this.severityLevel = severityLevel; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getFindingType() { return findingType; }
    public void setFindingType(String findingType) { this.findingType = findingType; }
    
    public String getCweId() { return cweId; }
    public void setCweId(String cweId) { this.cweId = cweId; }
    
    public Double getCvssScore() { return cvssScore; }
    public void setCvssScore(Double cvssScore) { this.cvssScore = cvssScore; }
    
    public String getCvssVector() { return cvssVector; }
    public void setCvssVector(String cvssVector) { this.cvssVector = cvssVector; }
    
    public String getEvidence() { return evidence; }
    public void setEvidence(String evidence) { this.evidence = evidence; }
    
    public String getExploitDetails() { return exploitDetails; }
    public void setExploitDetails(String exploitDetails) { this.exploitDetails = exploitDetails; }
    
    public String getImpactDescription() { return impactDescription; }
    public void setImpactDescription(String impactDescription) { this.impactDescription = impactDescription; }
    
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    
    public String getRemediationSteps() { return remediationSteps; }
    public void setRemediationSteps(String remediationSteps) { this.remediationSteps = remediationSteps; }
    
    public String getReferences() { return references; }
    public void setReferences(String references) { this.references = references; }
    
    public String getAffectedEndpoint() { return affectedEndpoint; }
    public void setAffectedEndpoint(String affectedEndpoint) { this.affectedEndpoint = affectedEndpoint; }
    
    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
    
    public String getParameterName() { return parameterName; }
    public void setParameterName(String parameterName) { this.parameterName = parameterName; }
    
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    
    public Integer getResponseStatusCode() { return responseStatusCode; }
    public void setResponseStatusCode(Integer responseStatusCode) { this.responseStatusCode = responseStatusCode; }
    
    public String getResponseData() { return responseData; }
    public void setResponseData(String responseData) { this.responseData = responseData; }
    
    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }
    
    public Boolean getIsFalsePositive() { return isFalsePositive; }
    public void setIsFalsePositive(Boolean isFalsePositive) { this.isFalsePositive = isFalsePositive; }
    
    public Boolean getRequiresManualVerification() { return requiresManualVerification; }
    public void setRequiresManualVerification(Boolean requiresManualVerification) { this.requiresManualVerification = requiresManualVerification; }
    
    public LocalDateTime getDiscoveredAt() { return discoveredAt; }
    public void setDiscoveredAt(LocalDateTime discoveredAt) { this.discoveredAt = discoveredAt; }
    
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
    
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurityFinding that = (SecurityFinding) o;
        return Objects.equals(findingTitle, that.findingTitle) &&
               Objects.equals(owaspCategory, that.owaspCategory) &&
               Objects.equals(affectedEndpoint, that.affectedEndpoint) &&
               Objects.equals(payload, that.payload);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(findingTitle, owaspCategory, affectedEndpoint, payload);
    }
    
    @Override
    public String toString() {
        return "SecurityFinding{" +
                "id=" + id +
                ", findingTitle='" + findingTitle + '\'' +
                ", owaspCategory=" + owaspCategory +
                ", severityLevel=" + severityLevel +
                ", status='" + status + '\'' +
                ", findingType='" + findingType + '\'' +
                ", affectedEndpoint='" + affectedEndpoint + '\'' +
                '}';
    }
}
