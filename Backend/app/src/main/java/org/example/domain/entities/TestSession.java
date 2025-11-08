package org.example.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain entity for Test Sessions
 * Represents a testing session that can execute tests against artifacts
 */
@Entity
@Table(name = "test_sessions")
public class TestSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Session ID is required")
    @Column(name = "session_id", unique = true, nullable = false)
    private String sessionId;
    
    @NotBlank(message = "Name is required")
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionStatus status = SessionStatus.CREATED;
    
    @Column(name = "executor")
    private String executor;
    
    @Column(name = "environment")
    private String environment;
    
    @Column(name = "test_type")
    private String testType;
    
    @Lob
    @Column(name = "test_config", columnDefinition = "TEXT")
    private String testConfig;
    
    @Lob
    @Column(name = "test_results", columnDefinition = "TEXT")
    private String testResults;
    
    @Lob
    @Column(name = "execution_log", columnDefinition = "TEXT")
    private String executionLog;
    
    @ElementCollection
    @CollectionTable(name = "test_session_artifacts", joinColumns = @JoinColumn(name = "test_session_id"))
    @Column(name = "artifact_id")
    private List<String> artifactIds = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "test_session_tags", joinColumns = @JoinColumn(name = "test_session_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
    
    @Column(name = "progress_percentage", nullable = false)
    private Integer progressPercentage = 0;
    
    @Column(name = "tests_total")
    private Integer testsTotal = 0;
    
    @Column(name = "tests_passed")
    private Integer testsPassed = 0;
    
    @Column(name = "tests_failed")
    private Integer testsFailed = 0;
    
    @Column(name = "tests_skipped")
    private Integer testsSkipped = 0;
    
    @Column(name = "execution_duration_ms")
    private Long executionDurationMs;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "timeout_minutes")
    private Integer timeoutMinutes;
    
    @Column(name = "retry_on_failure", nullable = false)
    private Boolean retryOnFailure = false;
    
    @Column(name = "parallel_execution", nullable = false)
    private Boolean parallelExecution = false;
    
    @NotNull(message = "Creation timestamp is required")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    // Constructors
    public TestSession() {
        this.sessionId = generateUniqueId();
        this.createdAt = LocalDateTime.now();
    }
    
    public TestSession(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }
    
    public TestSession(String name, String description, String executor) {
        this(name, description);
        this.executor = executor;
    }
    
    // PrePersist and PreUpdate callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (sessionId == null || sessionId.trim().isEmpty()) {
            sessionId = generateUniqueId();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    private String generateUniqueId() {
        return "session-" + UUID.randomUUID().toString();
    }
    
    public void start() {
        this.status = SessionStatus.RUNNING;
        this.startTime = LocalDateTime.now();
        this.isActive = true;
    }
    
    public void pause() {
        if (this.status == SessionStatus.RUNNING) {
            this.status = SessionStatus.PAUSED;
        }
    }
    
    public void resume() {
        if (this.status == SessionStatus.PAUSED) {
            this.status = SessionStatus.RUNNING;
        }
    }
    
    public void complete() {
        this.status = SessionStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
        this.isActive = false;
        calculateExecutionDuration();
    }
    
    public void fail() {
        this.status = SessionStatus.FAILED;
        this.endTime = LocalDateTime.now();
        this.isActive = false;
        calculateExecutionDuration();
    }
    
    public void cancel() {
        this.status = SessionStatus.CANCELLED;
        this.endTime = LocalDateTime.now();
        this.isActive = false;
        calculateExecutionDuration();
    }
    
    public void timeout() {
        this.status = SessionStatus.TIMEOUT;
        this.endTime = LocalDateTime.now();
        this.isActive = false;
        calculateExecutionDuration();
    }
    
    private void calculateExecutionDuration() {
        if (startTime != null && endTime != null) {
            this.executionDurationMs = java.time.Duration.between(startTime, endTime).toMillis();
        }
    }
    
    public void addArtifactId(String artifactId) {
        if (!artifactIds.contains(artifactId)) {
            artifactIds.add(artifactId);
        }
    }
    
    public void removeArtifactId(String artifactId) {
        artifactIds.remove(artifactId);
    }
    
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void removeTag(String tag) {
        tags.remove(tag);
    }
    
    public void updateProgress(int passed, int failed, int skipped, int total) {
        this.testsPassed = passed;
        this.testsFailed = failed;
        this.testsSkipped = skipped;
        this.testsTotal = total;
        
        if (total > 0) {
            this.progressPercentage = (int) ((double) (passed + failed + skipped) / total * 100);
        }
    }
    
    public void incrementPassedTests() {
        this.testsPassed++;
        updateProgressPercentage();
    }
    
    public void incrementFailedTests() {
        this.testsFailed++;
        updateProgressPercentage();
    }
    
    public void incrementSkippedTests() {
        this.testsSkipped++;
        updateProgressPercentage();
    }
    
    private void updateProgressPercentage() {
        if (testsTotal > 0) {
            this.progressPercentage = (int) ((double) (testsPassed + testsFailed + testsSkipped) / testsTotal * 100);
        }
    }
    
    public void activate() {
        this.isActive = true;
    }
    
    public void deactivate() {
        this.isActive = false;
    }
    
    public boolean isActive() {
        return isActive != null && isActive;
    }
    
    public boolean isRunning() {
        return status == SessionStatus.RUNNING;
    }
    
    public boolean isCompleted() {
        return status == SessionStatus.COMPLETED;
    }
    
    public boolean hasFailed() {
        return status == SessionStatus.FAILED;
    }
    
    public boolean isTimedOut() {
        return status == SessionStatus.TIMEOUT;
    }
    
    public boolean isCancelled() {
        return status == SessionStatus.CANCELLED;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public SessionStatus getStatus() {
        return status;
    }
    
    public void setStatus(SessionStatus status) {
        this.status = status;
    }
    
    public String getExecutor() {
        return executor;
    }
    
    public void setExecutor(String executor) {
        this.executor = executor;
    }
    
    public String getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(String environment) {
        this.environment = environment;
    }
    
    public String getTestType() {
        return testType;
    }
    
    public void setTestType(String testType) {
        this.testType = testType;
    }
    
    public String getTestConfig() {
        return testConfig;
    }
    
    public void setTestConfig(String testConfig) {
        this.testConfig = testConfig;
    }
    
    public String getTestResults() {
        return testResults;
    }
    
    public void setTestResults(String testResults) {
        this.testResults = testResults;
    }
    
    public String getExecutionLog() {
        return executionLog;
    }
    
    public void setExecutionLog(String executionLog) {
        this.executionLog = executionLog;
    }
    
    public List<String> getArtifactIds() {
        return artifactIds;
    }
    
    public void setArtifactIds(List<String> artifactIds) {
        this.artifactIds = artifactIds;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public Integer getProgressPercentage() {
        return progressPercentage;
    }
    
    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
    
    public Integer getTestsTotal() {
        return testsTotal;
    }
    
    public void setTestsTotal(Integer testsTotal) {
        this.testsTotal = testsTotal;
    }
    
    public Integer getTestsPassed() {
        return testsPassed;
    }
    
    public void setTestsPassed(Integer testsPassed) {
        this.testsPassed = testsPassed;
    }
    
    public Integer getTestsFailed() {
        return testsFailed;
    }
    
    public void setTestsFailed(Integer testsFailed) {
        this.testsFailed = testsFailed;
    }
    
    public Integer getTestsSkipped() {
        return testsSkipped;
    }
    
    public void setTestsSkipped(Integer testsSkipped) {
        this.testsSkipped = testsSkipped;
    }
    
    public Long getExecutionDurationMs() {
        return executionDurationMs;
    }
    
    public void setExecutionDurationMs(Long executionDurationMs) {
        this.executionDurationMs = executionDurationMs;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public Integer getTimeoutMinutes() {
        return timeoutMinutes;
    }
    
    public void setTimeoutMinutes(Integer timeoutMinutes) {
        this.timeoutMinutes = timeoutMinutes;
    }
    
    public Boolean getRetryOnFailure() {
        return retryOnFailure;
    }
    
    public void setRetryOnFailure(Boolean retryOnFailure) {
        this.retryOnFailure = retryOnFailure;
    }
    
    public Boolean getParallelExecution() {
        return parallelExecution;
    }
    
    public void setParallelExecution(Boolean parallelExecution) {
        this.parallelExecution = parallelExecution;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    // Enums
    public enum SessionStatus {
        CREATED,
        RUNNING,
        PAUSED,
        COMPLETED,
        FAILED,
        CANCELLED,
        TIMEOUT
    }
    
    @Override
    public String toString() {
        return "TestSession{" +
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestSession that = (TestSession) o;
        return sessionId != null && sessionId.equals(that.sessionId);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}