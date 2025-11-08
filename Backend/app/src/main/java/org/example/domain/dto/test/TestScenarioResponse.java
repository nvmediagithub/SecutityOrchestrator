package org.example.domain.dto.test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO для ответов с данными тестовых сценариев
 */
public class TestScenarioResponse {
    
    private Long id;
    private String scenarioId;
    private String name;
    private String description;
    private String scenarioType;
    private String owaspCategory;
    private String analysisId;
    private String applicationId;
    private String environment;
    
    private Integer priority;
    private String severity;
    private String riskLevel;
    private String status; // DRAFT, ACTIVE, INACTIVE, DEPRECATED
    
    private Boolean isAutomated;
    private Boolean isParallelExecution;
    private Boolean isSecurityTest;
    private Boolean isFunctionalTest;
    private Boolean isPerformanceTest;
    
    private List<String> testSteps;
    private List<String> testDataSets;
    private List<String> prerequisites;
    private List<String> dependencies;
    
    private Map<String, String> configuration;
    private Map<String, String> expectedResults;
    private Map<String, String> assertions;
    
    private String targetEndpoints;
    private String targetOperations;
    private String attackVectors;
    private String securityControls;
    
    private Integer estimatedDuration;
    private Integer maxRetries;
    private Integer timeoutSeconds;
    
    private List<String> tags;
    private List<String> affectedComponents;
    private List<String> relatedIssues;
    
    private String createdBy;
    private String lastModifiedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastExecuted;
    
    private String notes;
    private String businessImpact;
    private String technicalNotes;
    
    // Execution statistics
    private Integer totalExecutions = 0;
    private Integer successfulExecutions = 0;
    private Integer failedExecutions = 0;
    private Double successRate = 0.0;
    private Long averageExecutionTime = 0L;
    
    private String categoryDescription;
    private String owaspDescription;
    private List<String> complianceStandards;
    
    // Constructors
    public TestScenarioResponse() {}
    
    public TestScenarioResponse(Long id, String scenarioId, String name, String scenarioType) {
        this.id = id;
        this.scenarioId = scenarioId;
        this.name = name;
        this.scenarioType = scenarioType;
    }
    
    // Helper methods
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    public boolean isSecurityFocused() {
        return isSecurityTest != null && isSecurityTest;
    }
    
    public boolean hasRecentExecution() {
        if (lastExecuted == null) return false;
        return lastExecuted.isAfter(LocalDateTime.now().minusDays(7));
    }
    
    public double getSuccessPercentage() {
        if (totalExecutions == 0) return 0.0;
        return (double) successfulExecutions / totalExecutions * 100.0;
    }
    
    public void calculateSuccessRate() {
        if (totalExecutions > 0) {
            this.successRate = (double) successfulExecutions / totalExecutions;
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
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
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
    
    public String getCategoryDescription() { return categoryDescription; }
    public void setCategoryDescription(String categoryDescription) { this.categoryDescription = categoryDescription; }
    
    public String getOwaspDescription() { return owaspDescription; }
    public void setOwaspDescription(String owaspDescription) { this.owaspDescription = owaspDescription; }
    
    public List<String> getComplianceStandards() { return complianceStandards; }
    public void setComplianceStandards(List<String> complianceStandards) { this.complianceStandards = complianceStandards; }
}