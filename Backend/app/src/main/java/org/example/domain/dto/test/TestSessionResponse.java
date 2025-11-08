package org.example.domain.dto.test;

import org.example.domain.entities.TestSession;

import java.time.LocalDateTime;
import java.util.List;

public class TestSessionResponse {
    
    private Long id;
    private String sessionId;
    private String name;
    private String description;
    private TestSession.SessionStatus status;
    private String executor;
    private String environment;
    private String testType;
    private String testConfig;
    private String testResults;
    private String executionLog;
    private List<String> artifactIds;
    private List<String> tags;
    private Integer progressPercentage;
    private Integer testsTotal;
    private Integer testsPassed;
    private Integer testsFailed;
    private Integer testsSkipped;
    private Long executionDurationMs;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer timeoutMinutes;
    private Boolean retryOnFailure;
    private Boolean parallelExecution;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
    
    public TestSessionResponse() {
    }
    
    public TestSessionResponse(TestSession testSession) {
        if (testSession != null) {
            this.id = testSession.getId();
            this.sessionId = testSession.getSessionId();
            this.name = testSession.getName();
            this.description = testSession.getDescription();
            this.status = testSession.getStatus();
            this.executor = testSession.getExecutor();
            this.environment = testSession.getEnvironment();
            this.testType = testSession.getTestType();
            this.testConfig = testSession.getTestConfig();
            this.testResults = testSession.getTestResults();
            this.executionLog = testSession.getExecutionLog();
            this.artifactIds = testSession.getArtifactIds();
            this.tags = testSession.getTags();
            this.progressPercentage = testSession.getProgressPercentage();
            this.testsTotal = testSession.getTestsTotal();
            this.testsPassed = testSession.getTestsPassed();
            this.testsFailed = testSession.getTestsFailed();
            this.testsSkipped = testSession.getTestsSkipped();
            this.executionDurationMs = testSession.getExecutionDurationMs();
            this.startTime = testSession.getStartTime();
            this.endTime = testSession.getEndTime();
            this.timeoutMinutes = testSession.getTimeoutMinutes();
            this.retryOnFailure = testSession.getRetryOnFailure();
            this.parallelExecution = testSession.getParallelExecution();
            this.createdAt = testSession.getCreatedAt();
            this.updatedAt = testSession.getUpdatedAt();
            this.isActive = testSession.getIsActive();
        }
    }
    
    public static TestSessionResponse from(TestSession testSession) {
        return new TestSessionResponse(testSession);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public TestSession.SessionStatus getStatus() { return status; }
    public void setStatus(TestSession.SessionStatus status) { this.status = status; }
    
    public String getExecutor() { return executor; }
    public void setExecutor(String executor) { this.executor = executor; }
    
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    
    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }
    
    public String getTestConfig() { return testConfig; }
    public void setTestConfig(String testConfig) { this.testConfig = testConfig; }
    
    public String getTestResults() { return testResults; }
    public void setTestResults(String testResults) { this.testResults = testResults; }
    
    public String getExecutionLog() { return executionLog; }
    public void setExecutionLog(String executionLog) { this.executionLog = executionLog; }
    
    public List<String> getArtifactIds() { return artifactIds; }
    public void setArtifactIds(List<String> artifactIds) { this.artifactIds = artifactIds; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }
    
    public Integer getTestsTotal() { return testsTotal; }
    public void setTestsTotal(Integer testsTotal) { this.testsTotal = testsTotal; }
    
    public Integer getTestsPassed() { return testsPassed; }
    public void setTestsPassed(Integer testsPassed) { this.testsPassed = testsPassed; }
    
    public Integer getTestsFailed() { return testsFailed; }
    public void setTestsFailed(Integer testsFailed) { this.testsFailed = testsFailed; }
    
    public Integer getTestsSkipped() { return testsSkipped; }
    public void setTestsSkipped(Integer testsSkipped) { this.testsSkipped = testsSkipped; }
    
    public Long getExecutionDurationMs() { return executionDurationMs; }
    public void setExecutionDurationMs(Long executionDurationMs) { this.executionDurationMs = executionDurationMs; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public Integer getTimeoutMinutes() { return timeoutMinutes; }
    public void setTimeoutMinutes(Integer timeoutMinutes) { this.timeoutMinutes = timeoutMinutes; }
    
    public Boolean getRetryOnFailure() { return retryOnFailure; }
    public void setRetryOnFailure(Boolean retryOnFailure) { this.retryOnFailure = retryOnFailure; }
    
    public Boolean getParallelExecution() { return parallelExecution; }
    public void setParallelExecution(Boolean parallelExecution) { this.parallelExecution = parallelExecution; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    @Override
    public String toString() {
        return "TestSessionResponse{" +
                "id=" + id +
                ", sessionId='" + sessionId + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", executor='" + executor + '\'' +
                ", progressPercentage=" + progressPercentage +
                ", testsPassed=" + testsPassed +
                ", testsFailed=" + testsFailed +
                ", testsTotal=" + testsTotal +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}