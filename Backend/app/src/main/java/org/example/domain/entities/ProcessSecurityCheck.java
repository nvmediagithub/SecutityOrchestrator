package org.example.domain.entities;

import org.example.domain.valueobjects.BpmnProcessId;
import org.example.domain.valueobjects.ProcessIssueSeverity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing security checks performed in business processes
 */
public class ProcessSecurityCheck {
    private final String checkId;
    private final BpmnProcessId processId;
    private final String checkType;
    private final String checkName;
    private final String checkDescription;
    private final String targetElement;
    private final boolean passed;
    private final String failureReason;
    private final ProcessIssueSeverity severity;
    private final String recommendation;
    private final LocalDateTime performedAt;

    public ProcessSecurityCheck(String checkId, BpmnProcessId processId, String checkType,
                              String checkName, String checkDescription, String targetElement,
                              boolean passed, String failureReason, ProcessIssueSeverity severity,
                              String recommendation, LocalDateTime performedAt) {
        this.checkId = Objects.requireNonNull(checkId, "Check ID cannot be null");
        this.processId = Objects.requireNonNull(processId, "Process ID cannot be null");
        this.checkType = Objects.requireNonNull(checkType, "Check type cannot be null");
        this.checkName = Objects.requireNonNull(checkName, "Check name cannot be null");
        this.checkDescription = Objects.requireNonNull(checkDescription, "Check description cannot be null");
        this.targetElement = targetElement;
        this.passed = passed;
        this.failureReason = failureReason;
        this.severity = severity;
        this.recommendation = recommendation;
        this.performedAt = Objects.requireNonNull(performedAt, "Performed timestamp cannot be null");
    }

    /**
     * Creates a new security check
     */
    public static ProcessSecurityCheck createCheck(BpmnProcessId processId, String checkType,
                                                 String checkName, String checkDescription) {
        return new ProcessSecurityCheck(
            "check-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000),
            processId, checkType, checkName, checkDescription, null,
            false, null, null, null, LocalDateTime.now()
        );
    }

    /**
     * Creates a passed security check
     */
    public static ProcessSecurityCheck createPassedCheck(BpmnProcessId processId, String checkType,
                                                       String checkName, String checkDescription,
                                                       String targetElement) {
        return new ProcessSecurityCheck(
            "check-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000),
            processId, checkType, checkName, checkDescription, targetElement,
            true, null, null, null, LocalDateTime.now()
        );
    }

    /**
     * Creates a failed security check
     */
    public static ProcessSecurityCheck createFailedCheck(BpmnProcessId processId, String checkType,
                                                       String checkName, String checkDescription,
                                                       String targetElement, String failureReason,
                                                       ProcessIssueSeverity severity, String recommendation) {
        return new ProcessSecurityCheck(
            "check-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000),
            processId, checkType, checkName, checkDescription, targetElement,
            false, failureReason, severity, recommendation, LocalDateTime.now()
        );
    }

    // Getters
    public String getCheckId() { return checkId; }
    public BpmnProcessId getProcessId() { return processId; }
    public String getCheckType() { return checkType; }
    public String getCheckName() { return checkName; }
    public String getCheckDescription() { return checkDescription; }
    public String getTargetElement() { return targetElement; }
    public boolean isPassed() { return passed; }
    public String getFailureReason() { return failureReason; }
    public ProcessIssueSeverity getSeverity() { return severity; }
    public String getRecommendation() { return recommendation; }
    public LocalDateTime getPerformedAt() { return performedAt; }

    // Business logic methods
    public boolean hasTargetElement() {
        return targetElement != null && !targetElement.trim().isEmpty();
    }

    public boolean hasFailureReason() {
        return failureReason != null && !failureReason.trim().isEmpty();
    }

    public boolean hasSeverity() {
        return severity != null;
    }

    public boolean hasRecommendation() {
        return recommendation != null && !recommendation.trim().isEmpty();
    }

    public boolean isFailed() {
        return !passed;
    }

    public boolean isAuthenticationCheck() {
        return "authentication".equalsIgnoreCase(checkType);
    }

    public boolean isAuthorizationCheck() {
        return "authorization".equalsIgnoreCase(checkType);
    }

    public boolean isDataValidationCheck() {
        return "data_validation".equalsIgnoreCase(checkType);
    }

    public boolean isEncryptionCheck() {
        return "encryption".equalsIgnoreCase(checkType);
    }

    public boolean isAuditCheck() {
        return "audit".equalsIgnoreCase(checkType);
    }

    public boolean isCriticalSeverity() {
        return severity != null && severity == ProcessIssueSeverity.CRITICAL;
    }

    public boolean isHighSeverity() {
        return severity != null && severity == ProcessIssueSeverity.HIGH;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessSecurityCheck that = (ProcessSecurityCheck) o;
        return Objects.equals(checkId, that.checkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkId);
    }

    @Override
    public String toString() {
        return "ProcessSecurityCheck{" +
                "checkId='" + checkId + '\'' +
                ", processId=" + processId +
                ", checkType='" + checkType + '\'' +
                ", checkName='" + checkName + '\'' +
                ", passed=" + passed +
                ", targetElement='" + targetElement + '\'' +
                '}';
    }
}