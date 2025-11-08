package org.example.domain.dto.bpmn;

import org.example.domain.entities.ProcessIssue;

import java.time.LocalDateTime;

/**
 * Response DTO for ProcessIssue entity
 */
public class ProcessIssueResponse {
    private String issueId;
    private String issueType;
    private String severity;
    private String issueTitle;
    private String issueDescription;
    private String affectedElement;
    private String solution;
    private LocalDateTime detectedAt;

    public ProcessIssueResponse() {}

    public ProcessIssueResponse(String issueId, String issueType, String severity,
                              String issueTitle, String issueDescription, String affectedElement,
                              String solution, LocalDateTime detectedAt) {
        this.issueId = issueId;
        this.issueType = issueType;
        this.severity = severity;
        this.issueTitle = issueTitle;
        this.issueDescription = issueDescription;
        this.affectedElement = affectedElement;
        this.solution = solution;
        this.detectedAt = detectedAt;
    }

    /**
     * Creates a response from a ProcessIssue domain object
     */
    public static ProcessIssueResponse fromDomain(ProcessIssue issue) {
        return new ProcessIssueResponse(
            issue.getIssueId(),
            issue.getIssueType().name(),
            issue.getSeverity().name(),
            issue.getIssueTitle(),
            issue.getIssueDescription(),
            issue.getAffectedElement(),
            issue.getSolution(),
            issue.getDetectedAt()
        );
    }

    // Getters and setters
    public String getIssueId() { return issueId; }
    public void setIssueId(String issueId) { this.issueId = issueId; }

    public String getIssueType() { return issueType; }
    public void setIssueType(String issueType) { this.issueType = issueType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getIssueTitle() { return issueTitle; }
    public void setIssueTitle(String issueTitle) { this.issueTitle = issueTitle; }

    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }

    public String getAffectedElement() { return affectedElement; }
    public void setAffectedElement(String affectedElement) { this.affectedElement = affectedElement; }

    public String getSolution() { return solution; }
    public void setSolution(String solution) { this.solution = solution; }

    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }

    // Utility methods
    public boolean isCritical() {
        return "CRITICAL".equals(severity);
    }

    public boolean isHighPriority() {
        return "HIGH".equals(severity);
    }

    public boolean isSecurityIssue() {
        return "SECURITY".equals(issueType);
    }
}