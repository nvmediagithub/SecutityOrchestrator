package org.example.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.domain.valueobjects.OwaspTestCategory;
import org.example.domain.valueobjects.SeverityLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Main entity representing a security test for OWASP API Security
 */
@Entity
@Table(name = "security_tests")
public class SecurityTest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Test name is required")
    @Column(name = "test_name", nullable = false)
    private String testName;
    
    @Column(name = "test_description", columnDefinition = "TEXT")
    private String testDescription;
    
    @NotNull(message = "OWASP category is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "owasp_category", nullable = false)
    private OwaspTestCategory owaspCategory;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "severity_level", nullable = false)
    private SeverityLevel severityLevel = SeverityLevel.MEDIUM;
    
    @Column(name = "test_type", nullable = false)
    private String testType; // FUNCTIONAL, SECURITY, PERFORMANCE, INTEGRATION
    
    @Column(name = "status", nullable = false)
    private String status; // DRAFT, READY, RUNNING, COMPLETED, FAILED, CANCELLED
    
    @Lob
    @Column(name = "test_script", columnDefinition = "TEXT")
    private String testScript; // The actual test code/script
    
    @Lob
    @Column(name = "test_configuration", columnDefinition = "TEXT")
    private String testConfiguration; // JSON configuration for the test
    
    @Lob
    @Column(name = "test_data", columnDefinition = "TEXT")
    private String testData; // Test data in JSON format
    
    @Column(name = "target_endpoint", columnDefinition = "TEXT")
    private String targetEndpoint; // API endpoint to test
    
    @Column(name = "http_method")
    private String httpMethod; // GET, POST, PUT, DELETE, etc.
    
    @Column(name = "target_openapi_id")
    private String targetOpenApiId; // Link to OpenAPI specification
    
    @Column(name = "target_bpmn_id")
    private String targetBpmnId; // Link to BPMN process
    
    @Column(name = "target_analysis_id")
    private String targetAnalysisId; // Link to LLM analysis
    
    @Column(name = "expected_result", columnDefinition = "TEXT")
    private String expectedResult; // What the test should find/detect
    
    @Column(name = "vulnerability_type", columnDefinition = "TEXT")
    private String vulnerabilityType; // Type of vulnerability being tested
    
    @Column(name = "exploit_technique", columnDefinition = "TEXT")
    private String exploitTechnique; // How to exploit the vulnerability
    
    @Column(name = "test_priority", nullable = false)
    private Integer testPriority = 50; // 1-100, higher is more important
    
    @Column(name = "estimated_duration_seconds")
    private Integer estimatedDurationSeconds; // How long the test takes
    
    @Column(name = "max_retries", nullable = false)
    private Integer maxRetries = 3;
    
    @Column(name = "is_critical", nullable = false)
    private Boolean isCritical = false;
    
    @Column(name = "is_automated", nullable = false)
    private Boolean isAutomated = true;
    
    @Column(name = "requires_manual_review", nullable = false)
    private Boolean requiresManualReview = false;
    
    @Column(name = "compliance_standard", columnDefinition = "TEXT")
    private String complianceStandard; // OWASP, NIST, etc.
    
    @Lob
    @Column(name = "prerequisites", columnDefinition = "TEXT")
    private String prerequisites; // What needs to be set up before test
    
    @Lob
    @Column(name = "cleanup_actions", columnDefinition = "TEXT")
    private String cleanupActions; // What to clean up after test
    
    @Lob
    @Column(name = "references", columnDefinition = "TEXT")
    private String references; // URLs, documentation, etc.
    
    @ElementCollection
    @CollectionTable(name = "security_test_tags", joinColumns = @JoinColumn(name = "security_test_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
    
    @OneToMany(mappedBy = "securityTest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SecurityFinding> findings = new ArrayList<>();
    
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
    public SecurityTest() {
        this.createdAt = LocalDateTime.now();
        this.status = "DRAFT";
    }
    
    public SecurityTest(String testName, OwaspTestCategory owaspCategory, String testType) {
        this();
        this.testName = testName;
        this.owaspCategory = owaspCategory;
        this.testType = testType;
    }
    
    // Business methods
    public void markAsReady() {
        this.status = "READY";
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsRunning() {
        this.status = "RUNNING";
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsCompleted() {
        this.status = "COMPLETED";
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsFailed() {
        this.status = "FAILED";
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void removeTag(String tag) {
        tags.remove(tag);
    }
    
    public void addFinding(SecurityFinding finding) {
        findings.add(finding);
        finding.setSecurityTest(this);
    }
    
    public void removeFinding(SecurityFinding finding) {
        findings.remove(finding);
        finding.setSecurityTest(null);
    }
    
    public boolean isReady() {
        return "READY".equals(status) && testScript != null && !testScript.trim().isEmpty();
    }
    
    public boolean isRunning() {
        return "RUNNING".equals(status);
    }
    
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    public boolean hasFindings() {
        return !findings.isEmpty();
    }
    
    public long getCriticalFindingsCount() {
        return findings.stream()
                .filter(finding -> SeverityLevel.CRITICAL.equals(finding.getSeverityLevel()))
                .count();
    }
    
    public long getHighFindingsCount() {
        return findings.stream()
                .filter(finding -> SeverityLevel.HIGH.equals(finding.getSeverityLevel()))
                .count();
    }
    
    public double getVulnerabilityScore() {
        if (findings.isEmpty()) return 0.0;
        
        double totalScore = findings.stream()
                .mapToDouble(finding -> getSeverityScore(finding.getSeverityLevel()))
                .sum();
        
        return Math.min(totalScore / findings.size(), 10.0); // Max score of 10
    }
    
    private double getSeverityScore(SeverityLevel severity) {
        switch (severity) {
            case CRITICAL: return 10.0;
            case HIGH: return 7.0;
            case MEDIUM: return 4.0;
            case LOW: return 1.0;
            default: return 0.0;
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }
    
    public String getTestDescription() { return testDescription; }
    public void setTestDescription(String testDescription) { this.testDescription = testDescription; }
    
    public OwaspTestCategory getOwaspCategory() { return owaspCategory; }
    public void setOwaspCategory(OwaspTestCategory owaspCategory) { this.owaspCategory = owaspCategory; }
    
    public SeverityLevel getSeverityLevel() { return severityLevel; }
    public void setSeverityLevel(SeverityLevel severityLevel) { this.severityLevel = severityLevel; }
    
    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getTestScript() { return testScript; }
    public void setTestScript(String testScript) { this.testScript = testScript; }
    
    public String getTestConfiguration() { return testConfiguration; }
    public void setTestConfiguration(String testConfiguration) { this.testConfiguration = testConfiguration; }
    
    public String getTestData() { return testData; }
    public void setTestData(String testData) { this.testData = testData; }
    
    public String getTargetEndpoint() { return targetEndpoint; }
    public void setTargetEndpoint(String targetEndpoint) { this.targetEndpoint = targetEndpoint; }
    
    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
    
    public String getTargetOpenApiId() { return targetOpenApiId; }
    public void setTargetOpenApiId(String targetOpenApiId) { this.targetOpenApiId = targetOpenApiId; }
    
    public String getTargetBpmnId() { return targetBpmnId; }
    public void setTargetBpmnId(String targetBpmnId) { this.targetBpmnId = targetBpmnId; }
    
    public String getTargetAnalysisId() { return targetAnalysisId; }
    public void setTargetAnalysisId(String targetAnalysisId) { this.targetAnalysisId = targetAnalysisId; }
    
    public String getExpectedResult() { return expectedResult; }
    public void setExpectedResult(String expectedResult) { this.expectedResult = expectedResult; }
    
    public String getVulnerabilityType() { return vulnerabilityType; }
    public void setVulnerabilityType(String vulnerabilityType) { this.vulnerabilityType = vulnerabilityType; }
    
    public String getExploitTechnique() { return exploitTechnique; }
    public void setExploitTechnique(String exploitTechnique) { this.exploitTechnique = exploitTechnique; }
    
    public Integer getTestPriority() { return testPriority; }
    public void setTestPriority(Integer testPriority) { this.testPriority = testPriority; }
    
    public Integer getEstimatedDurationSeconds() { return estimatedDurationSeconds; }
    public void setEstimatedDurationSeconds(Integer estimatedDurationSeconds) { this.estimatedDurationSeconds = estimatedDurationSeconds; }
    
    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
    
    public Boolean getIsCritical() { return isCritical; }
    public void setIsCritical(Boolean isCritical) { this.isCritical = isCritical; }
    
    public Boolean getIsAutomated() { return isAutomated; }
    public void setIsAutomated(Boolean isAutomated) { this.isAutomated = isAutomated; }
    
    public Boolean getRequiresManualReview() { return requiresManualReview; }
    public void setRequiresManualReview(Boolean requiresManualReview) { this.requiresManualReview = requiresManualReview; }
    
    public String getComplianceStandard() { return complianceStandard; }
    public void setComplianceStandard(String complianceStandard) { this.complianceStandard = complianceStandard; }
    
    public String getPrerequisites() { return prerequisites; }
    public void setPrerequisites(String prerequisites) { this.prerequisites = prerequisites; }
    
    public String getCleanupActions() { return cleanupActions; }
    public void setCleanupActions(String cleanupActions) { this.cleanupActions = cleanupActions; }
    
    public String getReferences() { return references; }
    public void setReferences(String references) { this.references = references; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags != null ? tags : new ArrayList<>(); }
    
    public List<SecurityFinding> getFindings() { return findings; }
    public void setFindings(List<SecurityFinding> findings) { 
        this.findings = findings != null ? findings : new ArrayList<>();
    }
    
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
        SecurityTest that = (SecurityTest) o;
        return Objects.equals(testName, that.testName) &&
               Objects.equals(owaspCategory, that.owaspCategory) &&
               Objects.equals(targetOpenApiId, that.targetOpenApiId) &&
               Objects.equals(targetBpmnId, that.targetBpmnId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(testName, owaspCategory, targetOpenApiId, targetBpmnId);
    }
    
    @Override
    public String toString() {
        return "SecurityTest{" +
                "id=" + id +
                ", testName='" + testName + '\'' +
                ", owaspCategory=" + owaspCategory +
                ", severityLevel=" + severityLevel +
                ", status='" + status + '\'' +
                ", vulnerabilityType='" + vulnerabilityType + '\'' +
                '}';
    }
}
