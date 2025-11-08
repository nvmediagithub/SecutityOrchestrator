package org.example.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Сущность для хранения тестовых сценариев OWASP
 */
@Entity
@Table(name = "test_scenarios")
public class TestScenario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String scenarioId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 2000)
    private String description;
    
    @Column(length = 1000)
    private String scenarioType; // SECURITY, FUNCTIONAL, PERFORMANCE, INTEGRATION, REGRESSION
    
    @Column(length = 1000)
    private String owaspCategory; // A01, A02, A03, etc. или API01, API02, etc.
    
    @Column(length = 1000)
    private String analysisId; // Связь с анализом OpenAPI/BPMN
    
    @Column(length = 1000)
    private String applicationId;
    
    @Column(length = 1000)
    private String environment; // DEV, TEST, STAGING, PROD
    
    @Column(length = 1000)
    private String status = "DRAFT"; // DRAFT, ACTIVE, INACTIVE, DEPRECATED
    
    private Integer priority = 3; // 1-5 scale
    private String severity; // INFO, LOW, MEDIUM, HIGH, CRITICAL
    private String riskLevel; // MINIMAL, LOW, MEDIUM, HIGH, CRITICAL
    
    private Boolean isAutomated = true;
    private Boolean isParallelExecution = false;
    private Boolean isSecurityTest = true;
    private Boolean isFunctionalTest = false;
    private Boolean isPerformanceTest = false;
    
    @ElementCollection
    private List<String> testSteps = new ArrayList<>();
    
    @ElementCollection
    private List<String> testDataSets = new ArrayList<>();
    
    @ElementCollection
    private List<String> prerequisites = new ArrayList<>();
    
    @ElementCollection
    private List<String> dependencies = new ArrayList<>();
    
    @ElementCollection
    private Map<String, String> configuration = new HashMap<>();
    
    @ElementCollection
    private Map<String, String> expectedResults = new HashMap<>();
    
    @ElementCollection
    private Map<String, String> assertions = new HashMap<>();
    
    @Column(length = 2000)
    private String targetEndpoints;
    
    @Column(length = 2000)
    private String targetOperations;
    
    @Column(length = 2000)
    private String attackVectors;
    
    @Column(length = 2000)
    private String securityControls;
    
    private Integer estimatedDuration; // в минутах
    private Integer maxRetries = 3;
    private Integer timeoutSeconds = 300;
    
    @ElementCollection
    private List<String> tags = new ArrayList<>();
    
    @ElementCollection
    private List<String> affectedComponents = new ArrayList<>();
    
    @ElementCollection
    private List<String> relatedIssues = new ArrayList<>();
    
    @Column(length = 2000)
    private String createdBy;
    
    @Column(length = 2000)
    private String lastModifiedBy;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastExecuted;
    
    @Column(length = 4000)
    private String notes;
    
    @Column(length = 4000)
    private String businessImpact;
    
    @Column(length = 4000)
    private String technicalNotes;
    
    // Execution statistics
    private Integer totalExecutions = 0;
    private Integer successfulExecutions = 0;
    private Integer failedExecutions = 0;
    private Double successRate = 0.0;
    private Long averageExecutionTime = 0L;
    
    // Constructors
    public TestScenario() {
        this.scenarioId = "TS_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public TestScenario(String name, String scenarioType, String owaspCategory) {
        this();
        this.name = name;
        this.scenarioType = scenarioType;
        this.owaspCategory = owaspCategory;
    }
    
    // Helper methods
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    public boolean isSecurityFocused() {
        return isSecurityTest != null && isSecurityTest;
    }
    
    public void calculateSuccessRate() {
        if (totalExecutions > 0) {
            this.successRate = (double) successfulExecutions / totalExecutions;
        }
    }
    
    public void incrementExecution(boolean success) {
        this.totalExecutions++;
        if (success) {
            this.successfulExecutions++;
        } else {
            this.failedExecutions++;
        }
        this.lastExecuted = LocalDateTime.now();
        calculateSuccessRate();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addTag(String tag) {
        if (tag != null && !tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getScenarioId() { return scenarioId; }
    public void setScenarioId(String scenarioId) { this.scenarioId = scenarioId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getScenarioType() { return scenarioType; }
    public void setScenarioType(String scenarioType) { this.scenarioType = scenarioType; }
    
    public String getOwaspCategory() { return owaspCategory; }
    public void setOwaspCategory(String owaspCategory) { this.owaspCategory = owaspCategory; }
    
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    
    public Boolean getIsAutomated() { return isAutomated; }
    public void setIsAutomated(Boolean isAutomated) { this.isAutomated = isAutomated; }
    
    public Boolean getIsParallelExecution() { return isParallelExecution; }
    public void setIsParallelExecution(Boolean isParallelExecution) { this.isParallelExecution = isParallelExecution; }
    
    public Boolean getIsSecurityTest() { return isSecurityTest; }
    public void setIsSecurityTest(Boolean isSecurityTest) { this.isSecurityTest = isSecurityTest; }
    
    public Boolean getIsFunctionalTest() { return isFunctionalTest; }
    public void setIsFunctionalTest(Boolean isFunctionalTest) { this.isFunctionalTest = isFunctionalTest; }
    
    public Boolean getIsPerformanceTest() { return isPerformanceTest; }
    public void setIsPerformanceTest(Boolean isPerformanceTest) { this.isPerformanceTest = isPerformanceTest; }
    
    public List<String> getTestSteps() { return testSteps; }
    public void setTestSteps(List<String> testSteps) { this.testSteps = testSteps; }
    
    public List<String> getTestDataSets() { return testDataSets; }
    public void setTestDataSets(List<String> testDataSets) { this.testDataSets = testDataSets; }
    
    public List<String> getPrerequisites() { return prerequisites; }
    public void setPrerequisites(List<String> prerequisites) { this.prerequisites = prerequisites; }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    
    public Map<String, String> getConfiguration() { return configuration; }
    public void setConfiguration(Map<String, String> configuration) { this.configuration = configuration; }
    
    public Map<String, String> getExpectedResults() { return expectedResults; }
    public void setExpectedResults(Map<String, String> expectedResults) { this.expectedResults = expectedResults; }
    
    public Map<String, String> getAssertions() { return assertions; }
    public void setAssertions(Map<String, String> assertions) { this.assertions = assertions; }
    
    public String getTargetEndpoints() { return targetEndpoints; }
    public void setTargetEndpoints(String targetEndpoints) { this.targetEndpoints = targetEndpoints; }
    
    public String getTargetOperations() { return targetOperations; }
    public void setTargetOperations(String targetOperations) { this.targetOperations = targetOperations; }
    
    public String getAttackVectors() { return attackVectors; }
    public void setAttackVectors(String attackVectors) { this.attackVectors = attackVectors; }
    
    public String getSecurityControls() { return securityControls; }
    public void setSecurityControls(String securityControls) { this.securityControls = securityControls; }
    
    public Integer getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(Integer estimatedDuration) { this.estimatedDuration = estimatedDuration; }
    
    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
    
    public Integer getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(Integer timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public List<String> getAffectedComponents() { return affectedComponents; }
    public void setAffectedComponents(List<String> affectedComponents) { this.affectedComponents = affectedComponents; }
    
    public List<String> getRelatedIssues() { return relatedIssues; }
    public void setRelatedIssues(List<String> relatedIssues) { this.relatedIssues = relatedIssues; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getLastExecuted() { return lastExecuted; }
    public void setLastExecuted(LocalDateTime lastExecuted) { this.lastExecuted = lastExecuted; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getBusinessImpact() { return businessImpact; }
    public void setBusinessImpact(String businessImpact) { this.businessImpact = businessImpact; }
    
    public String getTechnicalNotes() { return technicalNotes; }
    public void setTechnicalNotes(String technicalNotes) { this.technicalNotes = technicalNotes; }
    
    public Integer getTotalExecutions() { return totalExecutions; }
    public void setTotalExecutions(Integer totalExecutions) { this.totalExecutions = totalExecutions; }
    
    public Integer getSuccessfulExecutions() { return successfulExecutions; }
    public void setSuccessfulExecutions(Integer successfulExecutions) { this.successfulExecutions = successfulExecutions; }
    
    public Integer getFailedExecutions() { return failedExecutions; }
    public void setFailedExecutions(Integer failedExecutions) { this.failedExecutions = failedExecutions; }
    
    public Double getSuccessRate() { return successRate; }
    public void setSuccessRate(Double successRate) { this.successRate = successRate; }
    
    public Long getAverageExecutionTime() { return averageExecutionTime; }
    public void setAverageExecutionTime(Long averageExecutionTime) { this.averageExecutionTime = averageExecutionTime; }
    
    @Override
    public String toString() {
        return "TestScenario{" +
                "scenarioId='" + scenarioId + '\'' +
                ", name='" + name + '\'' +
                ", scenarioType='" + scenarioType + '\'' +
                ", owaspCategory='" + owaspCategory + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestScenario that = (TestScenario) o;
        return Objects.equals(scenarioId, that.scenarioId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(scenarioId);
    }
}