package org.example.domain.dto.bpmn;

import org.example.domain.entities.ProcessIssue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Response DTO for a list of process issues
 */
public class ProcessIssueListResponse {
    private String processId;
    private List<ProcessIssueResponse> issues;
    private int totalCount;
    private int criticalCount;
    private int highCount;
    private int securityCount;
    private int logicErrorCount;
    private int performanceCount;
    private int complianceCount;
    private int validationCount;
    private LocalDateTime lastUpdated;

    public ProcessIssueListResponse() {}

    public ProcessIssueListResponse(String processId, List<ProcessIssueResponse> issues,
                                  int totalCount, int criticalCount, int highCount,
                                  int securityCount, int logicErrorCount, int performanceCount,
                                  int complianceCount, int validationCount, LocalDateTime lastUpdated) {
        this.processId = processId;
        this.issues = issues;
        this.totalCount = totalCount;
        this.criticalCount = criticalCount;
        this.highCount = highCount;
        this.securityCount = securityCount;
        this.logicErrorCount = logicErrorCount;
        this.performanceCount = performanceCount;
        this.complianceCount = complianceCount;
        this.validationCount = validationCount;
        this.lastUpdated = lastUpdated;
    }

    /**
     * Creates a response from a list of ProcessIssue domain objects
     */
    public static ProcessIssueListResponse fromDomain(String processId, List<ProcessIssue> issues) {
        List<ProcessIssueResponse> issueResponses = issues != null ?
            issues.stream().map(ProcessIssueResponse::fromDomain).collect(Collectors.toList()) : 
            List.of();

        int criticalCount = (int) issues.stream()
            .filter(issue -> "CRITICAL".equals(issue.getSeverity().name())).count();
        int highCount = (int) issues.stream()
            .filter(issue -> "HIGH".equals(issue.getSeverity().name())).count();
        int securityCount = (int) issues.stream()
            .filter(issue -> "SECURITY".equals(issue.getIssueType().name())).count();
        int logicErrorCount = (int) issues.stream()
            .filter(issue -> "LOGIC_ERROR".equals(issue.getIssueType().name())).count();
        int performanceCount = (int) issues.stream()
            .filter(issue -> "PERFORMANCE".equals(issue.getIssueType().name())).count();
        int complianceCount = (int) issues.stream()
            .filter(issue -> "COMPLIANCE".equals(issue.getIssueType().name())).count();
        int validationCount = (int) issues.stream()
            .filter(issue -> "VALIDATION".equals(issue.getIssueType().name())).count();

        return new ProcessIssueListResponse(
            processId,
            issueResponses,
            issues != null ? issues.size() : 0,
            criticalCount,
            highCount,
            securityCount,
            logicErrorCount,
            performanceCount,
            complianceCount,
            validationCount,
            LocalDateTime.now()
        );
    }

    // Getters and setters
    public String getProcessId() { return processId; }
    public void setProcessId(String processId) { this.processId = processId; }

    public List<ProcessIssueResponse> getIssues() { return issues; }
    public void setIssues(List<ProcessIssueResponse> issues) { 
        this.issues = issues;
        calculateCounts();
    }

    public int getTotalCount() { return totalCount; }
    public int getCriticalCount() { return criticalCount; }
    public int getHighCount() { return highCount; }
    public int getSecurityCount() { return securityCount; }
    public int getLogicErrorCount() { return logicErrorCount; }
    public int getPerformanceCount() { return performanceCount; }
    public int getComplianceCount() { return complianceCount; }
    public int getValidationCount() { return validationCount; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    private void calculateCounts() {
        this.totalCount = issues != null ? issues.size() : 0;
        this.criticalCount = (int) issues.stream()
            .filter(issue -> "CRITICAL".equals(issue.getSeverity())).count();
        this.highCount = (int) issues.stream()
            .filter(issue -> "HIGH".equals(issue.getSeverity())).count();
        this.securityCount = (int) issues.stream()
            .filter(issue -> "SECURITY".equals(issue.getIssueType())).count();
        this.logicErrorCount = (int) issues.stream()
            .filter(issue -> "LOGIC_ERROR".equals(issue.getIssueType())).count();
        this.performanceCount = (int) issues.stream()
            .filter(issue -> "PERFORMANCE".equals(issue.getIssueType())).count();
        this.complianceCount = (int) issues.stream()
            .filter(issue -> "COMPLIANCE".equals(issue.getIssueType())).count();
        this.validationCount = (int) issues.stream()
            .filter(issue -> "VALIDATION".equals(issue.getIssueType())).count();
    }

    // Utility methods
    public boolean hasCriticalIssues() {
        return criticalCount > 0;
    }

    public boolean hasHighPriorityIssues() {
        return highCount > 0;
    }

    public boolean hasSecurityIssues() {
        return securityCount > 0;
    }

    public boolean hasLogicErrors() {
        return logicErrorCount > 0;
    }

    public boolean hasPerformanceIssues() {
        return performanceCount > 0;
    }

    public boolean hasComplianceIssues() {
        return complianceCount > 0;
    }

    public boolean hasValidationErrors() {
        return validationCount > 0;
    }

    public boolean isEmpty() {
        return totalCount == 0;
    }
}