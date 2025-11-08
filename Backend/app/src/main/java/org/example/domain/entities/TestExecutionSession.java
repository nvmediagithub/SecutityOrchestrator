package org.example.domain.entities;

import org.example.domain.valueobjects.SeverityLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Сессия выполнения сквозных тестов - управляет полным циклом end-to-end тестирования
 * Интегрирует OpenAPI, BPMN, OWASP тесты и LLM анализ
 */
@Entity
@Table(name = "test_execution_sessions")
public class TestExecutionSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String sessionId;
    
    @Column(length = 1000, nullable = false)
    private String sessionName;
    
    @Column(length = 2000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private SeverityLevel overallSeverity = SeverityLevel.INFO;
    
    @Column(nullable = false)
    private String initiatedBy;
    
    @Column(length = 2000)
    private String executedBy;
    
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Long totalDurationMs;
    private Long preparationTimeMs;
    private Long executionTimeMs;
    private Long analysisTimeMs;
    private Long cleanupTimeMs;
    
    private Integer totalSteps = 0;
    private Integer completedSteps = 0;
    private Integer passedSteps = 0;
    private Integer failedSteps = 0;
    private Integer errorSteps = 0;
    private Integer skippedSteps = 0;
    
    @Column(length = 2000)
    private String openApiServiceId;
    
    @Column(length = 2000)
    private String bpmnProcessId;
    
    @Column(length = 2000)
    private String testDataContextId;
    
    @ElementCollection
    private List<String> owaspTestCategories = new ArrayList<>();
    
    @Column(length = 4000)
    private String executionConfig;
    
    @Column(length = 4000)
    private String llmAnalysisResult;
    
    @Column(length = 4000)
    private String comprehensiveReport;
    
    @Column(length = 4000)
    private String errorSummary;
    
    @Column(length = 2000)
    private String performanceMetrics;
    
    @Column(length = 2000)
    private String securityViolations;
    
    @Column(length = 2000)
    private String complianceStatus;
    
    @ElementCollection
    private List<String> executionSteps = new ArrayList<>();
    
    @ElementCollection
    private List<String> stepResults = new ArrayList<>();
    
    @ElementCollection
    private List<String> warnings = new ArrayList<>();
    
    @ElementCollection
    private List<String> criticalErrors = new ArrayList<>();
    
    @ElementCollection
    private List<String> generatedArtifacts = new ArrayList<>();
    
    @ElementCollection
    private List<String> capturedLogs = new ArrayList<>();
    
    private Boolean isParallelExecution = false;
    private Boolean isAutomatedExecution = false;
    private Boolean isScheduledExecution = false;
    private Boolean isDebugMode = false;
    
    private Integer retryCount = 0;
    private Integer maxRetries = 3;
    
    private String relatedSessionId;
    private String retryOfSessionId;
    
    @Column(length = 2000)
    private String environment;
    
    @Column(length = 4000)
    private String systemInformation;
    
    @Column(length = 2000)
    private String testEnvironmentConfig;
    
    @Column(length = 4000)
    private String metadata;
    
    @Column(length = 2000)
    private String notes;
    
    // Конструкторы
    public TestExecutionSession() {
        this.sessionId = "E2E_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.executionSteps = new ArrayList<>();
        this.stepResults = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.criticalErrors = new ArrayList<>();
        this.generatedArtifacts = new ArrayList<>();
        this.capturedLogs = new ArrayList<>();
    }
    
    public TestExecutionSession(String sessionName, String initiatedBy) {
        this();
        this.sessionName = sessionName;
        this.initiatedBy = initiatedBy;
        this.executedBy = initiatedBy;
    }
    
    // Вспомогательные методы
    public boolean isCompleted() {
        return status == SessionStatus.COMPLETED || 
               status == SessionStatus.FAILED || 
               status == SessionStatus.ERROR ||
               status == SessionStatus.CANCELLED;
    }
    
    public boolean isSuccess() {
        return status == SessionStatus.COMPLETED && failedSteps == 0;
    }
    
    public boolean hasErrors() {
        return !criticalErrors.isEmpty() || errorSteps > 0;
    }
    
    public double getSuccessRate() {
        if (totalSteps == 0) return 0.0;
        return (double) passedSteps / totalSteps * 100.0;
    }
    
    public double getCompletionRate() {
        if (totalSteps == 0) return 0.0;
        return (double) completedSteps / totalSteps * 100.0;
    }
    
    public void startSession() {
        this.status = SessionStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        addLog("Session started by: " + initiatedBy);
    }
    
    public void completeSession(boolean success) {
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        if (success && errorSteps == 0) {
            this.status = SessionStatus.COMPLETED;
            this.overallSeverity = SeverityLevel.INFO;
        } else if (errorSteps > 0) {
            this.status = SessionStatus.ERROR;
            this.overallSeverity = SeverityLevel.HIGH;
        } else {
            this.status = SessionStatus.FAILED;
            this.overallSeverity = SeverityLevel.MEDIUM;
        }
        
        if (this.startedAt != null && this.completedAt != null) {
            this.totalDurationMs = java.time.Duration.between(this.startedAt, this.completedAt).toMillis();
        }
        
        addLog("Session completed with status: " + status);
    }
    
    public void addStep(String stepName) {
        if (!executionSteps.contains(stepName)) {
            executionSteps.add(stepName);
            totalSteps++;
            addLog("Added step: " + stepName);
        }
    }
    
    public void completeStep(String stepName, boolean success) {
        completedSteps++;
        if (success) {
            passedSteps++;
        } else {
            failedSteps++;
        }
        addLog("Step completed: " + stepName + " - " + (success ? "PASSED" : "FAILED"));
    }
    
    public void addError(String error) {
        if (error != null && !criticalErrors.contains(error)) {
            criticalErrors.add(error);
            errorSteps++;
            this.overallSeverity = SeverityLevel.CRITICAL;
            addLog("Critical error added: " + error);
        }
    }
    
    public void addWarning(String warning) {
        if (warning != null && !warnings.contains(warning)) {
            warnings.add(warning);
            addLog("Warning added: " + warning);
        }
    }
    
    public void addLog(String logEntry) {
        if (logEntry != null) {
            String timestamp = LocalDateTime.now().toString();
            capturedLogs.add("[" + timestamp + "] " + logEntry);
        }
    }
    
    public void addArtifact(String artifact) {
        if (artifact != null && !generatedArtifacts.contains(artifact)) {
            generatedArtifacts.add(artifact);
        }
    }
    
    public void recordTiming(String phase, long durationMs) {
        switch (phase.toLowerCase()) {
            case "preparation":
                this.preparationTimeMs = durationMs;
                break;
            case "execution":
                this.executionTimeMs = durationMs;
                break;
            case "analysis":
                this.analysisTimeMs = durationMs;
                break;
            case "cleanup":
                this.cleanupTimeMs = durationMs;
                break;
        }
    }
    
    public void retry(String reason) {
        this.retryCount++;
        this.retryOfSessionId = this.sessionId;
        this.sessionId = this.sessionId + "_retry_" + retryCount;
        this.status = SessionStatus.PENDING;
        this.startedAt = null;
        this.completedAt = null;
        this.totalDurationMs = null;
        addLog("Session retried due to: " + reason);
    }
    
    public boolean canRetry() {
        return retryCount < maxRetries && (status == SessionStatus.ERROR || status == SessionStatus.FAILED);
    }
    
    // Enums
    public enum SessionStatus {
        PENDING,
        PREPARING,
        IN_PROGRESS,
        EXECUTING_TESTS,
        ANALYZING_RESULTS,
        GENERATING_REPORT,
        COMPLETED,
        FAILED,
        ERROR,
        CANCELLED,
        TIMEOUT
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getSessionName() { return sessionName; }
    public void setSessionName(String sessionName) { this.sessionName = sessionName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }
    
    public SeverityLevel getOverallSeverity() { return overallSeverity; }
    public void setOverallSeverity(SeverityLevel overallSeverity) { this.overallSeverity = overallSeverity; }
    
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
    
    public Long getAnalysisTimeMs() { return analysisTimeMs; }
    public void setAnalysisTimeMs(Long analysisTimeMs) { this.analysisTimeMs = analysisTimeMs; }
    
    public Long getCleanupTimeMs() { return cleanupTimeMs; }
    public void setCleanupTimeMs(Long cleanupTimeMs) { this.cleanupTimeMs = cleanupTimeMs; }
    
    public Integer getTotalSteps() { return totalSteps; }
    public void setTotalSteps(Integer totalSteps) { this.totalSteps = totalSteps; }
    
    public Integer getCompletedSteps() { return completedSteps; }
    public void setCompletedSteps(Integer completedSteps) { this.completedSteps = completedSteps; }
    
    public Integer getPassedSteps() { return passedSteps; }
    public void setPassedSteps(Integer passedSteps) { this.passedSteps = passedSteps; }
    
    public Integer getFailedSteps() { return failedSteps; }
    public void setFailedSteps(Integer failedSteps) { this.failedSteps = failedSteps; }
    
    public Integer getErrorSteps() { return errorSteps; }
    public void setErrorSteps(Integer errorSteps) { this.errorSteps = errorSteps; }
    
    public Integer getSkippedSteps() { return skippedSteps; }
    public void setSkippedSteps(Integer skippedSteps) { this.skippedSteps = skippedSteps; }
    
    public String getOpenApiServiceId() { return openApiServiceId; }
    public void setOpenApiServiceId(String openApiServiceId) { this.openApiServiceId = openApiServiceId; }
    
    public String getBpmnProcessId() { return bpmnProcessId; }
    public void setBpmnProcessId(String bpmnProcessId) { this.bpmnProcessId = bpmnProcessId; }
    
    public String getTestDataContextId() { return testDataContextId; }
    public void setTestDataContextId(String testDataContextId) { this.testDataContextId = testDataContextId; }
    
    public List<String> getOwaspTestCategories() { return owaspTestCategories; }
    public void setOwaspTestCategories(List<String> owaspTestCategories) { this.owaspTestCategories = owaspTestCategories; }
    
    public String getExecutionConfig() { return executionConfig; }
    public void setExecutionConfig(String executionConfig) { this.executionConfig = executionConfig; }
    
    public String getLlmAnalysisResult() { return llmAnalysisResult; }
    public void setLlmAnalysisResult(String llmAnalysisResult) { this.llmAnalysisResult = llmAnalysisResult; }
    
    public String getComprehensiveReport() { return comprehensiveReport; }
    public void setComprehensiveReport(String comprehensiveReport) { this.comprehensiveReport = comprehensiveReport; }
    
    public String getErrorSummary() { return errorSummary; }
    public void setErrorSummary(String errorSummary) { this.errorSummary = errorSummary; }
    
    public String getPerformanceMetrics() { return performanceMetrics; }
    public void setPerformanceMetrics(String performanceMetrics) { this.performanceMetrics = performanceMetrics; }
    
    public String getSecurityViolations() { return securityViolations; }
    public void setSecurityViolations(String securityViolations) { this.securityViolations = securityViolations; }
    
    public String getComplianceStatus() { return complianceStatus; }
    public void setComplianceStatus(String complianceStatus) { this.complianceStatus = complianceStatus; }
    
    public List<String> getExecutionSteps() { return executionSteps; }
    public void setExecutionSteps(List<String> executionSteps) { this.executionSteps = executionSteps; }
    
    public List<String> getStepResults() { return stepResults; }
    public void setStepResults(List<String> stepResults) { this.stepResults = stepResults; }
    
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    
    public List<String> getCriticalErrors() { return criticalErrors; }
    public void setCriticalErrors(List<String> criticalErrors) { this.criticalErrors = criticalErrors; }
    
    public List<String> getGeneratedArtifacts() { return generatedArtifacts; }
    public void setGeneratedArtifacts(List<String> generatedArtifacts) { this.generatedArtifacts = generatedArtifacts; }
    
    public List<String> getCapturedLogs() { return capturedLogs; }
    public void setCapturedLogs(List<String> capturedLogs) { this.capturedLogs = capturedLogs; }
    
    public Boolean getIsParallelExecution() { return isParallelExecution; }
    public void setIsParallelExecution(Boolean isParallelExecution) { this.isParallelExecution = isParallelExecution; }
    
    public Boolean getIsAutomatedExecution() { return isAutomatedExecution; }
    public void setIsAutomatedExecution(Boolean isAutomatedExecution) { this.isAutomatedExecution = isAutomatedExecution; }
    
    public Boolean getIsScheduledExecution() { return isScheduledExecution; }
    public void setIsScheduledExecution(Boolean isScheduledExecution) { this.isScheduledExecution = isScheduledExecution; }
    
    public Boolean getIsDebugMode() { return isDebugMode; }
    public void setIsDebugMode(Boolean isDebugMode) { this.isDebugMode = isDebugMode; }
    
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    
    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
    
    public String getRelatedSessionId() { return relatedSessionId; }
    public void setRelatedSessionId(String relatedSessionId) { this.relatedSessionId = relatedSessionId; }
    
    public String getRetryOfSessionId() { return retryOfSessionId; }
    public void setRetryOfSessionId(String retryOfSessionId) { this.retryOfSessionId = retryOfSessionId; }
    
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    
    public String getSystemInformation() { return systemInformation; }
    public void setSystemInformation(String systemInformation) { this.systemInformation = systemInformation; }
    
    public String getTestEnvironmentConfig() { return testEnvironmentConfig; }
    public void setTestEnvironmentConfig(String testEnvironmentConfig) { this.testEnvironmentConfig = testEnvironmentConfig; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    @Override
    public String toString() {
        return "TestExecutionSession{" +
                "sessionId='" + sessionId + '\'' +
                ", sessionName='" + sessionName + '\'' +
                ", status=" + status +
                ", totalSteps=" + totalSteps +
                ", passedSteps=" + passedSteps +
                ", failedSteps=" + failedSteps +
                ", overallSeverity=" + overallSeverity +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestExecutionSession that = (TestExecutionSession) o;
        return Objects.equals(sessionId, that.sessionId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }
}