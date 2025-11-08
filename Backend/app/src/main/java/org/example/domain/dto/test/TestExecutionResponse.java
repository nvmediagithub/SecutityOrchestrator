package org.example.domain.dto.test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class TestExecutionResponse {
    
    private Long id;
    private String executionId;
    private String testScenarioId;
    private String testSuiteId;
    private String environment;
    private String status;
    private String executionType;
    
    private String initiatedBy;
    private String executedBy;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Long totalDurationMs;
    private Long preparationTimeMs;
    private Long executionTimeMs;
    private Long cleanupTimeMs;
    
    private Integer totalSteps = 0;
    private Integer passedSteps = 0;
    private Integer failedSteps = 0;
    private Integer skippedSteps = 0;
    private Integer errorSteps = 0;
    
    private Integer totalAssertions = 0;
    private Integer passedAssertions = 0;
    private Integer failedAssertions = 0;
    
    private String overallSeverity;
    private String executionSummary;
    private String detailedResults;
    private String errorDetails;
    private String failureReason;
    
    private String performanceMetrics;
    private String securityViolations;
    private String complianceIssues;
    
    private List<String> executedSteps;
    private List<String> stepResults;
    private List<String> assertionResults;
    private List<String> warnings;
    private List<String> capturedLogs;
    private List<String> generatedArtifacts;
    
    private Boolean isParallelExecution = false;
    private Boolean isAutomatedExecution = false;
    private Boolean isScheduledExecution = false;
    private Boolean isDebugMode = false;
    
    private String executionConfig;
    private String dataSetUsed;
    private String variablesUsed;
    private String environmentVariables;
    private String systemInfo;
    private String metadata;
    
    private String relatedExecutionId;
    private String retryOfExecutionId;
    private Integer retryCount = 0;
    private Integer maxRetries = 3;
    
    private String notes;
    
    private Double successRate = 0.0;
    private Boolean isCompleted = false;
    private Boolean isSuccess = false;
    private Boolean isFailure = false;
    private Boolean hasWarnings = false;
    private Boolean hasErrors = false;
    
    public TestExecutionResponse() {}
    
    public TestExecutionResponse(String executionId, String testScenarioId) {
        this.executionId = executionId;
        this.testScenarioId = testScenarioId;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public boolean isSuccess() {
        return isSuccess;
    }
    
    public boolean isFailure() {
        return isFailure;
    }
    
    public boolean hasWarnings() {
        return hasWarnings;
    }
    
    public boolean hasErrors() {
        return hasErrors;
    }
    
    public void calculateMetrics() {
        if (totalSteps > 0) {
            this.successRate = (double) passedSteps / totalSteps;
        }
        this.isCompleted = "PASSED".equals(status) || "FAILED".equals(status) || 
                          "ERROR".equals(status) || "SKIPPED".equals(status) || 
                          "CANCELLED".equals(status);
        this.isSuccess = "PASSED".equals(status);
        this.isFailure = "FAILED".equals(status) || "ERROR".equals(status);
        this.hasWarnings = warnings != null && !warnings.isEmpty();
        this.hasErrors = errorDetails != null && !errorDetails.isEmpty();
    }
    
    public double getSuccessPercentage() {
        return successRate * 100.0;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getExecutionId() { return executionId; }
    public void setExecutionId(String executionId) { this.executionId = executionId; }
    
    public String getTestScenarioId() { return testScenarioId; }
    public void setTestScenarioId(String testScenarioId) { this.testScenarioId = testScenarioId; }
    
    public String getTestSuiteId() { return testSuiteId; }
    public void setTestSuiteId(String testSuiteId) { this.testSuiteId = testSuiteId; }
    
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getExecutionType() { return executionType; }
    public void setExecutionType(String executionType) { this.executionType = executionType; }
    
    public String getInitiatedBy() { return initiatedBy; }
    public void setInitiatedBy(String initiatedBy) { this.initiatedBy = initiatedBy; }
    
    public String getExecutedBy() { return executedBy; }
    public void setExecutedBy(String executedBy) { this.executedBy = executedBy; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Long getTotalDurationMs() { return totalDurationMs; }
    public void setTotalDurationMs(Long totalDurationMs) { this.totalDurationMs = totalDurationMs; }
    
    public Long getPreparationTimeMs() { return preparationTimeMs; }
    public void setPreparationTimeMs(Long preparationTimeMs) { this.preparationTimeMs = preparationTimeMs; }
    
    public Long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(Long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    
    public Long getCleanupTimeMs() { return cleanupTimeMs; }
    public void setCleanupTimeMs(Long cleanupTimeMs) { this.cleanupTimeMs = cleanupTimeMs; }
    
    public Integer getTotalSteps() { return totalSteps; }
    public void setTotalSteps(Integer totalSteps) { this.totalSteps = totalSteps; }
    
    public Integer getPassedSteps() { return passedSteps; }
    public void setPassedSteps(Integer passedSteps) { this.passedSteps = passedSteps; }
    
    public Integer getFailedSteps() { return failedSteps; }
    public void setFailedSteps(Integer failedSteps) { this.failedSteps = failedSteps; }
    
    public Integer getSkippedSteps() { return skippedSteps; }
    public void setSkippedSteps(Integer skippedSteps) { this.skippedSteps = skippedSteps; }
    
    public Integer getErrorSteps() { return errorSteps; }
    public void setErrorSteps(Integer errorSteps) { this.errorSteps = errorSteps; }
    
    public Integer getTotalAssertions() { return totalAssertions; }
    public void setTotalAssertions(Integer totalAssertions) { this.totalAssertions = totalAssertions; }
    
    public Integer getPassedAssertions() { return passedAssertions; }
    public void setPassedAssertions(Integer passedAssertions) { this.passedAssertions = passedAssertions; }
    
    public Integer getFailedAssertions() { return failedAssertions; }
    public void setFailedAssertions(Integer failedAssertions) { this.failedAssertions = failedAssertions; }
    
    public String getOverallSeverity() { return overallSeverity; }
    public void setOverallSeverity(String overallSeverity) { this.overallSeverity = overallSeverity; }
    
    public String getExecutionSummary() { return executionSummary; }
    public void setExecutionSummary(String executionSummary) { this.executionSummary = executionSummary; }
    
    public String getDetailedResults() { return detailedResults; }
    public void setDetailedResults(String detailedResults) { this.detailedResults = detailedResults; }
    
    public String getErrorDetails() { return errorDetails; }
    public void setErrorDetails(String errorDetails) { this.errorDetails = errorDetails; }
    
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    
    public String getPerformanceMetrics() { return performanceMetrics; }
    public void setPerformanceMetrics(String performanceMetrics) { this.performanceMetrics = performanceMetrics; }
    
    public String getSecurityViolations() { return securityViolations; }
    public void setSecurityViolations(String securityViolations) { this.securityViolations = securityViolations; }
    
    public String getComplianceIssues() { return complianceIssues; }
    public void setComplianceIssues(String complianceIssues) { this.complianceIssues = complianceIssues; }
    
    public List<String> getExecutedSteps() { return executedSteps; }
    public void setExecutedSteps(List<String> executedSteps) { this.executedSteps = executedSteps; }
    
    public List<String> getStepResults() { return stepResults; }
    public void setStepResults(List<String> stepResults) { this.stepResults = stepResults; }
    
    public List<String> getAssertionResults() { return assertionResults; }
    public void setAssertionResults(List<String> assertionResults) { this.assertionResults = assertionResults; }
    
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    
    public List<String> getCapturedLogs() { return capturedLogs; }
    public void setCapturedLogs(List<String> capturedLogs) { this.capturedLogs = capturedLogs; }
    
    public List<String> getGeneratedArtifacts() { return generatedArtifacts; }
    public void setGeneratedArtifacts(List<String> generatedArtifacts) { this.generatedArtifacts = generatedArtifacts; }
    
    public Boolean getIsParallelExecution() { return isParallelExecution; }
    public void setIsParallelExecution(Boolean isParallelExecution) { this.isParallelExecution = isParallelExecution; }
    
    public Boolean getIsAutomatedExecution() { return isAutomatedExecution; }
    public void setIsAutomatedExecution(Boolean isAutomatedExecution) { this.isAutomatedExecution = isAutomatedExecution; }
    
    public Boolean getIsScheduledExecution() { return isScheduledExecution; }
    public void setIsScheduledExecution(Boolean isScheduledExecution) { this.isScheduledExecution = isScheduledExecution; }
    
    public Boolean getIsDebugMode() { return isDebugMode; }
    public void setIsDebugMode(Boolean isDebugMode) { this.isDebugMode = isDebugMode; }
    
    public String getExecutionConfig() { return executionConfig; }
    public void setExecutionConfig(String executionConfig) { this.executionConfig = executionConfig; }
    
    public String getDataSetUsed() { return dataSetUsed; }
    public void setDataSetUsed(String dataSetUsed) { this.dataSetUsed = dataSetUsed; }
    
    public String getVariablesUsed() { return variablesUsed; }
    public void setVariablesUsed(String variablesUsed) { this.variablesUsed = variablesUsed; }
    
    public String getEnvironmentVariables() { return environmentVariables; }
    public void setEnvironmentVariables(String environmentVariables) { this.environmentVariables = environmentVariables; }
    
    public String getSystemInfo() { return systemInfo; }
    public void setSystemInfo(String systemInfo) { this.systemInfo = systemInfo; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    public String getRelatedExecutionId() { return relatedExecutionId; }
    public void setRelatedExecutionId(String relatedExecutionId) { this.relatedExecutionId = relatedExecutionId; }
    
    public String getRetryOfExecutionId() { return retryOfExecutionId; }
    public void setRetryOfExecutionId(String retryOfExecutionId) { this.retryOfExecutionId = retryOfExecutionId; }
    
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    
    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Double getSuccessRate() { return successRate; }
    public void setSuccessRate(Double successRate) { this.successRate = successRate; }
    
    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }
    
    public Boolean getIsSuccess() { return isSuccess; }
    public void setIsSuccess(Boolean isSuccess) { this.isSuccess = isSuccess; }
    
    public Boolean getIsFailure() { return isFailure; }
    public void setIsFailure(Boolean isFailure) { this.isFailure = isFailure; }
    
    public Boolean getHasWarnings() { return hasWarnings; }
    public void setHasWarnings(Boolean hasWarnings) { this.hasWarnings = hasWarnings; }
    
    public Boolean getHasErrors() { return hasErrors; }
    public void setHasErrors(Boolean hasErrors) { this.hasErrors = hasErrors; }
}