package org.example.shared.domain.entities;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Domain entity for TestSession
 * Represents a test execution session in the system
 */
public class TestSession {

    private Long id;
    private String sessionId;
    private String name;
    private String description;
    private String executor;
    private SessionStatus status = SessionStatus.CREATED;
    private boolean isActive = false;
    private String environment;
    private String testType;
    private Integer timeoutMinutes = 30;
    private boolean retryOnFailure = false;
    private boolean parallelExecution = false;
    private Set<String> tags = new HashSet<>();
    private List<String> artifactIds = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long executionDurationMs;
    private Integer testsTotal = 0;
    private Integer testsPassed = 0;
    private Integer testsFailed = 0;
    private Integer testsSkipped = 0;
    private Integer progressPercentage = 0;

    // Constructors
    public TestSession() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.sessionId = generateUniqueId();
    }

    public TestSession(String name, String description) {
        this();
        this.name = name;
        this.description = description;
        this.status = SessionStatus.CREATED;
    }

    public TestSession(String name, String description, String executor) {
        this(name, description);
        this.executor = executor;
    }

    // Business methods
    public void start() {
        this.status = SessionStatus.RUNNING;
        this.startTime = LocalDateTime.now();
        this.isActive = true;
        updateTimestamp();
    }

    public void stop() {
        if (this.status == SessionStatus.RUNNING) {
            this.status = SessionStatus.COMPLETED;
            this.endTime = LocalDateTime.now();
            calculateDuration();
        }
        this.isActive = false;
        updateTimestamp();
    }

    public void fail() {
        this.status = SessionStatus.FAILED;
        this.endTime = LocalDateTime.now();
        calculateDuration();
        this.isActive = false;
        updateTimestamp();
    }

    public void pause() {
        if (this.status == SessionStatus.RUNNING) {
            this.status = SessionStatus.PAUSED;
            updateTimestamp();
        }
    }

    public void resume() {
        if (this.status == SessionStatus.PAUSED) {
            this.status = SessionStatus.RUNNING;
            updateTimestamp();
        }
    }

    public void cancel() {
        this.status = SessionStatus.CANCELLED;
        this.endTime = LocalDateTime.now();
        calculateDuration();
        this.isActive = false;
        updateTimestamp();
    }

    public void updateProgress(int passed, int failed, int skipped, int total) {
        this.testsPassed = passed;
        this.testsFailed = failed;
        this.testsSkipped = skipped;
        this.testsTotal = total;
        this.progressPercentage = total > 0 ? (passed + failed + skipped) * 100 / total : 0;
        updateTimestamp();
    }

    public void addArtifact(String artifactId) {
        if (artifactId != null && !artifactId.trim().isEmpty()) {
            this.artifactIds.add(artifactId.trim());
            updateTimestamp();
        }
    }

    public void removeArtifact(String artifactId) {
        this.artifactIds.remove(artifactId);
        updateTimestamp();
    }

    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            this.tags.add(tag.trim());
            updateTimestamp();
        }
    }

    public void removeTag(String tag) {
        this.tags.remove(tag);
        updateTimestamp();
    }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    private void calculateDuration() {
        if (this.startTime != null && this.endTime != null) {
            this.executionDurationMs = java.time.Duration.between(this.startTime, this.endTime).toMillis();
        }
    }

    private String generateUniqueId() {
        return "SES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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
        updateTimestamp();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        updateTimestamp();
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
        updateTimestamp();
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
        updateTimestamp();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        updateTimestamp();
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
        updateTimestamp();
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
        updateTimestamp();
    }

    public Integer getTimeoutMinutes() {
        return timeoutMinutes;
    }

    public void setTimeoutMinutes(Integer timeoutMinutes) {
        this.timeoutMinutes = timeoutMinutes;
        updateTimestamp();
    }

    public boolean isRetryOnFailure() {
        return retryOnFailure;
    }

    public void setRetryOnFailure(boolean retryOnFailure) {
        this.retryOnFailure = retryOnFailure;
        updateTimestamp();
    }

    public boolean isParallelExecution() {
        return parallelExecution;
    }

    public void setParallelExecution(boolean parallelExecution) {
        this.parallelExecution = parallelExecution;
        updateTimestamp();
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
        updateTimestamp();
    }

    public List<String> getArtifactIds() {
        return artifactIds;
    }

    public void setArtifactIds(List<String> artifactIds) {
        this.artifactIds = artifactIds;
        updateTimestamp();
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

    public Long getExecutionDurationMs() {
        return executionDurationMs;
    }

    public void setExecutionDurationMs(Long executionDurationMs) {
        this.executionDurationMs = executionDurationMs;
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

    public Integer getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestSession that = (TestSession) o;
        return Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }

    @Override
    public String toString() {
        return "TestSession{" +
                "id=" + id +
                ", sessionId='" + sessionId + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", isActive=" + isActive +
                ", executor='" + executor + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    // Enum for session status
    public enum SessionStatus {
        CREATED,
        RUNNING,
        PAUSED,
        COMPLETED,
        FAILED,
        CANCELLED
    }
}