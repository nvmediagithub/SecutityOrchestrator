package org.example.domain.model.testdata;

import org.example.domain.valueobjects.TestCaseId;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a test execution step
 */
public class TestExecutionStep {

    private UUID id;
    private String name;
    private String description;
    private TestCaseId testCaseId;
    private int stepNumber;
    private StepType stepType;
    private StepStatus status;
    private String action;
    private String expectedResult;
    private String actualResult;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private long executionTimeMs;
    private String errorMessage;
    private Map<String, Object> testData;
    private Map<String, Object> metadata;

    public TestExecutionStep() {
        this.id = UUID.randomUUID();
        this.status = StepStatus.PENDING;
        this.startedAt = LocalDateTime.now();
    }

    public TestExecutionStep(String name, String description, TestCaseId testCaseId, int stepNumber, StepType stepType) {
        this();
        this.name = name;
        this.description = description;
        this.testCaseId = testCaseId;
        this.stepNumber = stepNumber;
        this.stepType = stepType;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public TestCaseId getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(TestCaseId testCaseId) {
        this.testCaseId = testCaseId;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public StepType getStepType() {
        return stepType;
    }

    public void setStepType(StepType stepType) {
        this.stepType = stepType;
    }

    public StepStatus getStatus() {
        return status;
    }

    public void setStatus(StepStatus status) {
        this.status = status;
        if (status == StepStatus.COMPLETED || status == StepStatus.FAILED) {
            this.completedAt = LocalDateTime.now();
            if (this.startedAt != null) {
                this.executionTimeMs = java.time.Duration.between(this.startedAt, this.completedAt).toMillis();
            }
        }
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public String getActualResult() {
        return actualResult;
    }

    public void setActualResult(String actualResult) {
        this.actualResult = actualResult;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, Object> getTestData() {
        return testData;
    }

    public void setTestData(Map<String, Object> testData) {
        this.testData = testData;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public boolean isCompleted() {
        return status == StepStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == StepStatus.FAILED;
    }

    public boolean isRunning() {
        return status == StepStatus.RUNNING;
    }

    public boolean isPending() {
        return status == StepStatus.PENDING;
    }

    /**
     * Step type enumeration
     */
    public enum StepType {
        SETUP,
        TEST_ACTION,
        VERIFICATION,
        ASSERTION,
        CLEANUP,
        VALIDATION
    }

    /**
     * Step status enumeration
     */
    public enum StepStatus {
        PENDING,
        RUNNING,
        COMPLETED,
        FAILED,
        SKIPPED
    }
}