package org.example.domain.entities;

import org.example.domain.valueobjects.SeverityLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Сущность для хранения проверок и ассертов в тестовых сценариях
 */
@Entity
@Table(name = "test_assertions")
public class TestAssertion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String assertionId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 2000)
    private String description;
    
    @Column(nullable = false)
    private String assertionType; // EQUALS, NOT_EQUALS, CONTAINS, NOT_CONTAINS, REGEX, JSON_PATH, XPATH, CUSTOM
    
    @Column(length = 4000)
    private String expectedValue;
    
    @Column(length = 4000)
    private String actualValue;
    
    @Column(length = 2000)
    private String sourcePath; // JSON path, XPath, CSS selector, etc.
    
    @Column(length = 1000)
    private String targetElement; // Element ID, field name, etc.
    
    @Column(length = 2000)
    private String validationMessage;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssertionStatus status = AssertionStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private SeverityLevel severity = SeverityLevel.INFO;
    
    @Column(length = 1000)
    private String testStepId;
    
    @Column(length = 1000)
    private String testScenarioId;
    
    private Boolean isCritical = false;
    private Boolean isOptional = false;
    private Boolean isDynamic = false;
    private Boolean isRetryable = true;
    
    @Column(length = 2000)
    private String customValidator; // Custom validation logic
    
    @Column(length = 2000)
    private String parameters; // JSON parameters for complex assertions
    
    @Column(length = 1000)
    private String timeoutSeconds = "30";
    
    @Column(length = 1000)
    private String retryCount = "0";
    
    @Column(length = 1000)
    private String retryIntervalMs = "1000";
    
    @Column(length = 2000)
    private String errorMessage;
    
    @Column(length = 4000)
    private String stackTrace;
    
    @ElementCollection
    private List<String> supportingData = new ArrayList<>();
    
    @ElementCollection
    private List<String> attachments = new ArrayList<>();
    
    @ElementCollection
    private Map<String, String> metadata = new HashMap<>();
    
    private LocalDateTime createdAt;
    private LocalDateTime executedAt;
    private Long executionTimeMs;
    
    @Column(length = 1000)
    private String executedBy;
    
    private Boolean isScreenshotCaptured = false;
    private Boolean isVideoRecorded = false;
    private Boolean isNetworkCaptured = false;
    
    @Column(length = 2000)
    private String screenshotPath;
    
    @Column(length = 2000)
    private String videoPath;
    
    @Column(length = 2000)
    private String networkLogPath;
    
    @Column(length = 4000)
    private String notes;
    
    // Constructors
    public TestAssertion() {
        this.assertionId = "TA_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.createdAt = LocalDateTime.now();
        this.supportingData = new ArrayList<>();
        this.attachments = new ArrayList<>();
        this.metadata = new HashMap<>();
    }
    
    public TestAssertion(String name, String assertionType, String expectedValue) {
        this();
        this.name = name;
        this.assertionType = assertionType;
        this.expectedValue = expectedValue;
    }
    
    // Helper methods
    public boolean isPassed() {
        return status == AssertionStatus.PASSED;
    }
    
    public boolean isFailed() {
        return status == AssertionStatus.FAILED;
    }
    
    public boolean isPending() {
        return status == AssertionStatus.PENDING;
    }
    
    public boolean isSkipped() {
        return status == AssertionStatus.SKIPPED;
    }
    
    public boolean isCriticalFailure() {
        return isFailed() && isCritical;
    }
    
    public void execute(String actualValue, boolean passed) {
        this.actualValue = actualValue;
        this.executedAt = LocalDateTime.now();
        this.status = passed ? AssertionStatus.PASSED : AssertionStatus.FAILED;
        this.executionTimeMs = System.currentTimeMillis();
        
        if (!passed && isCritical) {
            this.severity = SeverityLevel.CRITICAL;
        }
    }
    
    public void skip(String reason) {
        this.status = AssertionStatus.SKIPPED;
        this.executedAt = LocalDateTime.now();
        this.errorMessage = reason;
    }
    
    public void addSupportingData(String data) {
        if (data != null && !supportingData.contains(data)) {
            supportingData.add(data);
        }
    }
    
    public void addAttachment(String attachment) {
        if (attachment != null && !attachments.contains(attachment)) {
            attachments.add(attachment);
        }
    }
    
    public void addMetadata(String key, String value) {
        if (key != null && value != null) {
            metadata.put(key, value);
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAssertionId() { return assertionId; }
    public void setAssertionId(String assertionId) { this.assertionId = assertionId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getAssertionType() { return assertionType; }
    public void setAssertionType(String assertionType) { this.assertionType = assertionType; }
    
    public String getExpectedValue() { return expectedValue; }
    public void setExpectedValue(String expectedValue) { this.expectedValue = expectedValue; }
    
    public String getActualValue() { return actualValue; }
    public void setActualValue(String actualValue) { this.actualValue = actualValue; }
    
    public String getSourcePath() { return sourcePath; }
    public void setSourcePath(String sourcePath) { this.sourcePath = sourcePath; }
    
    public String getTargetElement() { return targetElement; }
    public void setTargetElement(String targetElement) { this.targetElement = targetElement; }
    
    public String getValidationMessage() { return validationMessage; }
    public void setValidationMessage(String validationMessage) { this.validationMessage = validationMessage; }
    
    public AssertionStatus getStatus() { return status; }
    public void setStatus(AssertionStatus status) { this.status = status; }
    
    public SeverityLevel getSeverity() { return severity; }
    public void setSeverity(SeverityLevel severity) { this.severity = severity; }
    
    public String getTestStepId() { return testStepId; }
    public void setTestStepId(String testStepId) { this.testStepId = testStepId; }
    
    public String getTestScenarioId() { return testScenarioId; }
    public void setTestScenarioId(String testScenarioId) { this.testScenarioId = testScenarioId; }
    
    public Boolean getIsCritical() { return isCritical; }
    public void setIsCritical(Boolean isCritical) { this.isCritical = isCritical; }
    
    public Boolean getIsOptional() { return isOptional; }
    public void setIsOptional(Boolean isOptional) { this.isOptional = isOptional; }
    
    public Boolean getIsDynamic() { return isDynamic; }
    public void setIsDynamic(Boolean isDynamic) { this.isDynamic = isDynamic; }
    
    public Boolean getIsRetryable() { return isRetryable; }
    public void setIsRetryable(Boolean isRetryable) { this.isRetryable = isRetryable; }
    
    public String getCustomValidator() { return customValidator; }
    public void setCustomValidator(String customValidator) { this.customValidator = customValidator; }
    
    public String getParameters() { return parameters; }
    public void setParameters(String parameters) { this.parameters = parameters; }
    
    public String getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(String timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
    
    public String getRetryCount() { return retryCount; }
    public void setRetryCount(String retryCount) { this.retryCount = retryCount; }
    
    public String getRetryIntervalMs() { return retryIntervalMs; }
    public void setRetryIntervalMs(String retryIntervalMs) { this.retryIntervalMs = retryIntervalMs; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String getStackTrace() { return stackTrace; }
    public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }
    
    public List<String> getSupportingData() { return supportingData; }
    public void setSupportingData(List<String> supportingData) { this.supportingData = supportingData; }
    
    public List<String> getAttachments() { return attachments; }
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }
    
    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }
    
    public Long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(Long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    
    public String getExecutedBy() { return executedBy; }
    public void setExecutedBy(String executedBy) { this.executedBy = executedBy; }
    
    public Boolean getIsScreenshotCaptured() { return isScreenshotCaptured; }
    public void setIsScreenshotCaptured(Boolean isScreenshotCaptured) { this.isScreenshotCaptured = isScreenshotCaptured; }
    
    public Boolean getIsVideoRecorded() { return isVideoRecorded; }
    public void setIsVideoRecorded(Boolean isVideoRecorded) { this.isVideoRecorded = isVideoRecorded; }
    
    public Boolean getIsNetworkCaptured() { return isNetworkCaptured; }
    public void setIsNetworkCaptured(Boolean isNetworkCaptured) { this.isNetworkCaptured = isNetworkCaptured; }
    
    public String getScreenshotPath() { return screenshotPath; }
    public void setScreenshotPath(String screenshotPath) { this.screenshotPath = screenshotPath; }
    
    public String getVideoPath() { return videoPath; }
    public void setVideoPath(String videoPath) { this.videoPath = videoPath; }
    
    public String getNetworkLogPath() { return networkLogPath; }
    public void setNetworkLogPath(String networkLogPath) { this.networkLogPath = networkLogPath; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    // Enums
    public enum AssertionStatus {
        PENDING,
        PASSED,
        FAILED,
        SKIPPED,
        ERROR,
        TIMEOUT
    }
    
    @Override
    public String toString() {
        return "TestAssertion{" +
                "assertionId='" + assertionId + '\'' +
                ", name='" + name + '\'' +
                ", assertionType='" + assertionType + '\'' +
                ", status=" + status +
                ", isCritical=" + isCritical +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestAssertion that = (TestAssertion) o;
        return Objects.equals(assertionId, that.assertionId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(assertionId);
    }
}