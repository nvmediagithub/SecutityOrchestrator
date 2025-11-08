package org.example.domain.entities;

import org.example.domain.valueobjects.BpmnProcessId;
import org.example.domain.valueobjects.ProcessIssueType;
import org.example.domain.valueobjects.ProcessIssueSeverity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing issues found in business processes
 */
public class ProcessIssue {
    private final String issueId;
    private final BpmnProcessId processId;
    private final ProcessIssueType issueType;
    private final ProcessIssueSeverity severity;
    private final String issueTitle;
    private final String issueDescription;
    private final String affectedElement;
    private final String solution;
    private final LocalDateTime detectedAt;

    public ProcessIssue(String issueId, BpmnProcessId processId, ProcessIssueType issueType,
                       ProcessIssueSeverity severity, String issueTitle, String issueDescription,
                       String affectedElement, String solution, LocalDateTime detectedAt) {
        this.issueId = Objects.requireNonNull(issueId, "Issue ID cannot be null");
        this.processId = Objects.requireNonNull(processId, "Process ID cannot be null");
        this.issueType = Objects.requireNonNull(issueType, "Issue type cannot be null");
        this.severity = Objects.requireNonNull(severity, "Severity cannot be null");
        this.issueTitle = Objects.requireNonNull(issueTitle, "Issue title cannot be null");
        this.issueDescription = Objects.requireNonNull(issueDescription, "Issue description cannot be null");
        this.affectedElement = affectedElement;
        this.solution = solution;
        this.detectedAt = Objects.requireNonNull(detectedAt, "Detected timestamp cannot be null");
    }

    /**
     * Creates a new process issue
     */
    public static ProcessIssue createIssue(BpmnProcessId processId, ProcessIssueType issueType,
                                         ProcessIssueSeverity severity, String issueTitle,
                                         String issueDescription) {
        return new ProcessIssue(
            "issue-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000),
            processId, issueType, severity, issueTitle, issueDescription,
            null, null, LocalDateTime.now()
        );
    }

    /**
     * Creates a process issue with affected element and solution
     */
    public static ProcessIssue createDetailedIssue(BpmnProcessId processId, ProcessIssueType issueType,
                                                  ProcessIssueSeverity severity, String issueTitle,
                                                  String issueDescription, String affectedElement,
                                                  String solution) {
        return new ProcessIssue(
            "issue-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000),
            processId, issueType, severity, issueTitle, issueDescription,
            affectedElement, solution, LocalDateTime.now()
        );
    }

    // Getters
    public String getIssueId() { return issueId; }
    public BpmnProcessId getProcessId() { return processId; }
    public ProcessIssueType getIssueType() { return issueType; }
    public ProcessIssueSeverity getSeverity() { return severity; }
    public String getIssueTitle() { return issueTitle; }
    public String getIssueDescription() { return issueDescription; }
    public String getAffectedElement() { return affectedElement; }
    public String getSolution() { return solution; }
    public LocalDateTime getDetectedAt() { return detectedAt; }

    // Business logic methods
    public boolean hasAffectedElement() {
        return affectedElement != null && !affectedElement.trim().isEmpty();
    }

    public boolean hasSolution() {
        return solution != null && !solution.trim().isEmpty();
    }

    public boolean isCritical() {
        return severity == ProcessIssueSeverity.CRITICAL;
    }

    public boolean isHighPriority() {
        return severity == ProcessIssueSeverity.HIGH;
    }

    public boolean isLogicError() {
        return issueType == ProcessIssueType.LOGIC_ERROR;
    }

    public boolean isSecurityIssue() {
        return issueType == ProcessIssueType.SECURITY;
    }

    public boolean isComplianceIssue() {
        return issueType == ProcessIssueType.COMPLIANCE;
    }

    public boolean isPerformanceIssue() {
        return issueType == ProcessIssueType.PERFORMANCE;
    }

    public boolean isValidationIssue() {
        return issueType == ProcessIssueType.VALIDATION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessIssue that = (ProcessIssue) o;
        return Objects.equals(issueId, that.issueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issueId);
    }

    @Override
    public String toString() {
        return "ProcessIssue{" +
                "issueId='" + issueId + '\'' +
                ", processId=" + processId +
                ", issueType=" + issueType +
                ", severity=" + severity +
                ", issueTitle='" + issueTitle + '\'' +
                ", affectedElement='" + affectedElement + '\'' +
                '}';
    }
}