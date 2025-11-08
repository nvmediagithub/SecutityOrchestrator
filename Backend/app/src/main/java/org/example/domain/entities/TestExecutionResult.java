package org.example.domain.entities;

import org.example.domain.valueobjects.SeverityLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Сущность для хранения детальных результатов выполнения тестовых сценариев
 */
@Entity
@Table(name = "test_execution_results")
public class TestExecutionResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String resultId;
    
    @Column(nullable = false)
    private String executionId; // Связь с TestExecution
    
    @Column(nullable = false)
    private String testScenarioId;
    
    @Column(nullable = false)
    private String testStepId;
    
    @Column(length = 1000)
    private String stepName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultStatus status = ResultStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private SeverityLevel severity = SeverityLevel.INFO;
    
    @Column(length = 4000)
    private String description;
    
    @Column(length = 4000)
    private String detailedMessage;
    
    @Column(length = 4000)
    private String errorDetails;
    
    @Column(length = 2000)
    private String errorCode;
    
    @Column(length = 2000)
    private String errorType; // ASSERTION_ERROR, TIMEOUT_ERROR, NETWORK_ERROR, etc.
    
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Long durationMs;
    
    @Column(length = 2000)
    private String executor; // Пользователь или система, выполнившая тест
    
    @Column(length = 2000)
    private String environment;
    
    @Column(length = 4000)
    private String inputData;
    
    @Column(length = 4000)
    private String outputData;
    
    @Column(length = 4000)
    private String expectedResult;
    
    @Column(length = 4000)
    private String actualResult;
    
    @Enumerated(EnumType.STRING)
    private ComparisonResult comparisonResult = ComparisonResult.NOT_COMPARED;
    
    @ElementCollection
    private List<String> assertions = new ArrayList<>();
    
    @ElementCollection
    private List<String> failedAssertions = new ArrayList<>();
    
    @ElementCollection
    private List<String> warnings = new ArrayList<>();
    
    @ElementCollection
    private List<String> capturedScreenshots = new ArrayList<>();
    
    @ElementCollection
    private List<String> generatedLogs = new ArrayList<>();
    
    @ElementCollection
    private List<String> createdArtifacts = new ArrayList<>();
    
    @ElementCollection
    private List<String> networkRequests = new ArrayList<>();
    
    @ElementCollection
    private List<String> databaseQueries = new ArrayList<>();
    
    @ElementCollection
    private Map<String, String> metrics = new HashMap<>();
    
    @ElementCollection
    private Map<String, String> performanceData = new HashMap<>();
    
    @ElementCollection
    private Map<String, String> securityChecks = new HashMap<>();
    
    private Integer retryCount = 0;
    private Integer maxRetries = 3;
    private Boolean isRetryAttempt = false;
    private String retryReason;
    
    private Boolean isParallelExecution = false;
    private String parallelExecutionId;
    private Integer parallelExecutionIndex;
    
    @Column(length = 2000)
    private String resourceUsage; // CPU, Memory, Disk usage
    
    @Column(length = 4000)
    private String systemInformation;
    
    @Column(length = 4000)
    private String browserInformation;
    
    @Column(length = 2000)
    private String testDataSetUsed;
    
    @Column(length = 4000)
    private String configuration;
    
    @ElementCollection
    private List<String> dependencies = new ArrayList<>();
    
    private Boolean isManualReviewRequired = false;
    private Boolean isAutomatedFlaky = false;
    private Boolean isKnownIssue = false;
    
    @Column(length = 2000)
    private String knownIssueId;
    
    @Column(length = 4000)
    private String notes;
    
    @Column(length = 2000)
    private String relatedIssue;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public TestExecutionResult() {
        this.resultId = "TER_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.assertions = new ArrayList<>();
        this.failedAssertions = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.capturedScreenshots = new ArrayList<>();
        this.generatedLogs = new ArrayList<>();
        this.createdArtifacts = new ArrayList<>();
        this.networkRequests = new ArrayList<>();
        this.databaseQueries = new ArrayList<>();
        this.metrics = new HashMap<>();
        this.performanceData = new HashMap<>();
        this.securityChecks = new HashMap<>();
        this.dependencies = new ArrayList<>();
    }
    
    public TestExecutionResult(String executionId, String testScenarioId, String testStepId) {
        this();
        this.executionId = executionId;
        this.testScenarioId = testScenarioId;
        this.testStepId = testStepId;
    }
    
    // Helper methods
    public boolean isPassed() {
        return status == ResultStatus.PASSED;
    }
    
    public boolean isFailed() {
        return status == ResultStatus.FAILED || status == ResultStatus.ERROR;
    }
    
    public boolean isCompleted() {
        return status == ResultStatus.PASSED || status == ResultStatus.FAILED || 
               status == ResultStatus.SKIPPED || status == ResultStatus.ERROR;
    }
    
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    public boolean hasFailedAssertions() {
        return !failedAssertions.isEmpty();
    }
    
    public void start() {
        this.status = ResultStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }
    
    public void complete(boolean success) {
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        if (success) {
            this.status = ResultStatus.PASSED;
            this.severity = SeverityLevel.INFO;
        } else {
            this.status = ResultStatus.FAILED;
            this.severity = SeverityLevel.HIGH;
        }
        
        if (this.startedAt != null && this.completedAt != null) {
            this.durationMs = java.time.Duration.between(this.startedAt, this.completedAt).toMillis();
        }
    }
    
    public void recordError(String errorMessage, String errorType) {
        this.status = ResultStatus.ERROR;
        this.errorDetails = errorMessage;
        this.errorType = errorType;
        this.completedAt = LocalDateTime.now();
        this.durationMs = startedAt != null ? 
            java.time.Duration.between(startedAt, completedAt).toMillis() : 0L;
        this.severity = SeverityLevel.CRITICAL;
    }
    
    public void addAssertion(String assertionId) {
        if (assertionId != null && !assertions.contains(assertionId)) {
            assertions.add(assertionId);
        }
    }
    
    public void addFailedAssertion(String assertionId) {
        if (assertionId != null && !failedAssertions.contains(assertionId)) {
            failedAssertions.add(assertionId);
        }
    }
    
    public void addWarning(String warning) {
        if (warning != null && !warnings.contains(warning)) {
            warnings.add(warning);
        }
    }
    
    public void addMetric(String key, String value) {
        if (key != null && value != null) {
            metrics.put(key, value);
        }
    }
    
    public void addPerformanceData(String key, String value) {
        if (key != null && value != null) {
            performanceData.put(key, value);
        }
    }
    
    public void addSecurityCheck(String check, String result) {
        if (check != null && result != null) {
            securityChecks.put(check, result);
        }
    }
    
    public void retry(String reason) {
        this.retryCount++;
        this.isRetryAttempt = true;
        this.retryReason = reason;
        this.status = ResultStatus.PENDING;
        this.startedAt = null;
        this.completedAt = null;
    }
    
    public boolean canRetry() {
        return retryCount < maxRetries && isRetryable();
    }
    
    public boolean isRetryable() {
        return status == ResultStatus.ERROR || 
               (status == ResultStatus.FAILED && hasFailedAssertions());
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getResultId() { return resultId; }
    public void setResultId(String resultId) { this.resultId = resultId; }
    
    public String getExecutionId() { return executionId; }
    public void setExecutionId(String executionId) { this.executionId = executionId; }
    
    public String getTestScenarioId() { return testScenarioId; }
    public void setTestScenarioId(String testScenarioId) { this.testScenarioId = testScenarioId; }
    
    public String getTestStepId() { return testStepId; }
    public void setTestStepId(String testStepId) { this.testStepId = testStepId; }
    
    public String getStepName() { return stepName; }
    public void setStepName(String stepName) { this.stepName = stepName; }
    
    public ResultStatus getStatus() { return status; }
    public void setStatus(ResultStatus status) { this.status = status; }
    
    public SeverityLevel getSeverity() { return severity; }
    public void setSeverity(SeverityLevel severity) { this.severity = severity; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getDetailedMessage() { return detailedMessage; }
    public void setDetailedMessage(String detailedMessage) { this.detailedMessage = detailedMessage; }
    
    public String getErrorDetails() { return errorDetails; }
    public void setErrorDetails(String errorDetails) { this.errorDetails = errorDetails; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public String getErrorType() { return errorType; }
    public void setErrorType(String errorType) { this.errorType = errorType; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
    
    public String getExecutor() { return executor; }
    public void setExecutor(String executor) { this.executor = executor; }
    
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    
    public String getInputData() { return inputData; }
    public void setInputData(String inputData) { this.inputData = inputData; }
    
    public String getOutputData() { return outputData; }
    public void setOutputData(String outputData) { this.outputData = outputData; }
    
    public String getExpectedResult() { return expectedResult; }
    public void setExpectedResult(String expectedResult) { this.expectedResult = expectedResult; }
    
    public String getActualResult() { return actualResult; }
    public void setActualResult(String actualResult) { this.actualResult = actualResult; }
    
    public ComparisonResult getComparisonResult() { return comparisonResult; }
    public void setComparisonResult(ComparisonResult comparisonResult) { this.comparisonResult = comparisonResult; }
    
    public List<String> getAssertions() { return assertions; }
    public void setAssertions(List<String> assertions) { this.assertions = assertions; }
    
    public List<String> getFailedAssertions() { return failedAssertions; }
    public void setFailedAssertions(List<String> failedAssertions) { this.failedAssertions = failedAssertions; }
    
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    
    public List<String> getCapturedScreenshots() { return capturedScreenshots; }
    public void setCapturedScreenshots(List<String> capturedScreenshots) { this.capturedScreenshots = capturedScreenshots; }
    
    public List<String> getGeneratedLogs() { return generatedLogs; }
    public void setGeneratedLogs(List<String> generatedLogs) { this.generatedLogs = generatedLogs; }
    
    public List<String> getCreatedArtifacts() { return createdArtifacts; }
    public void setCreatedArtifacts(List<String> createdArtifacts) { this.createdArtifacts = createdArtifacts; }
    
    public List<String> getNetworkRequests() { return networkRequests; }
    public void setNetworkRequests(List<String> networkRequests) { this.networkRequests = networkRequests; }
    
    public List<String> getDatabaseQueries() { return databaseQueries; }
    public void setDatabaseQueries(List<String> databaseQueries) { this.databaseQueries = databaseQueries; }
    
    public Map<String, String> getMetrics() { return metrics; }
    public void setMetrics(Map<String, String> metrics) { this.metrics = metrics; }
    
    public Map<String, String> getPerformanceData() { return performanceData; }
    public void setPerformanceData(Map<String, String> performanceData) { this.performanceData = performanceData; }
    
    public Map<String, String> getSecurityChecks() { return securityChecks; }
    public void setSecurityChecks(Map<String, String> securityChecks) { this.securityChecks = securityChecks; }
    
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    
    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
    
    public Boolean getIsRetryAttempt() { return isRetryAttempt; }
    public void setIsRetryAttempt(Boolean isRetryAttempt) { this.isRetryAttempt = isRetryAttempt; }
    
    public String getRetryReason() { return retryReason; }
    public void setRetryReason(String retryReason) { this.retryReason = retryReason; }
    
    public Boolean getIsParallelExecution() { return isParallelExecution; }
    public void setIsParallelExecution(Boolean isParallelExecution) { this.isParallelExecution = isParallelExecution; }
    
    public String getParallelExecutionId() { return parallelExecutionId; }
    public void setParallelExecutionId(String parallelExecutionId) { this.parallelExecutionId = parallelExecutionId; }
    
    public Integer getParallelExecutionIndex() { return parallelExecutionIndex; }
    public void setParallelExecutionIndex(Integer parallelExecutionIndex) { this.parallelExecutionIndex = parallelExecutionIndex; }
    
    public String getResourceUsage() { return resourceUsage; }
    public void setResourceUsage(String resourceUsage) { this.resourceUsage = resourceUsage; }
    
    public String getSystemInformation() { return systemInformation; }
    public void setSystemInformation(String systemInformation) { this.systemInformation = systemInformation; }
    
    public String getBrowserInformation() { return browserInformation; }
    public void setBrowserInformation(String browserInformation) { this.browserInformation = browserInformation; }
    
    public String getTestDataSetUsed() { return testDataSetUsed; }
    public void setTestDataSetUsed(String testDataSetUsed) { this.testDataSetUsed = testDataSetUsed; }
    
    public String getConfiguration() { return configuration; }
    public void setConfiguration(String configuration) { this.configuration = configuration; }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    
    public Boolean getIsManualReviewRequired() { return isManualReviewRequired; }
    public void setIsManualReviewRequired(Boolean isManualReviewRequired) { this.isManualReviewRequired = isManualReviewRequired; }
    
    public Boolean getIsAutomatedFlaky() { return isAutomatedFlaky; }
    public void setIsAutomatedFlaky(Boolean isAutomatedFlaky) { this.isAutomatedFlaky = isAutomatedFlaky; }
    
    public Boolean getIsKnownIssue() { return isKnownIssue; }
    public void setIsKnownIssue(Boolean isKnownIssue) { this.isKnownIssue = isKnownIssue; }
    
    public String getKnownIssueId() { return knownIssueId; }
    public void setKnownIssueId(String knownIssueId) { this.knownIssueId = knownIssueId; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getRelatedIssue() { return relatedIssue; }
    public void setRelatedIssue(String relatedIssue) { this.relatedIssue = relatedIssue; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Enums
    public enum ResultStatus {
        PENDING,
        IN_PROGRESS,
        PASSED,
        FAILED,
        SKIPPED,
        ERROR,
        TIMEOUT,
        CANCELLED
    }
    
    public enum ComparisonResult {
        NOT_COMPARED,
        MATCH,
        MISMATCH,
        PARTIAL_MATCH
    }
    
    @Override
    public String toString() {
        return "TestExecutionResult{" +
                "resultId='" + resultId + '\'' +
                ", executionId='" + executionId + '\'' +
                ", testStepId='" + testStepId + '\'' +
                ", status=" + status +
                ", severity=" + severity +
                ", durationMs=" + durationMs +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestExecutionResult that = (TestExecutionResult) o;
        return Objects.equals(resultId, that.resultId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(resultId);
    }
}