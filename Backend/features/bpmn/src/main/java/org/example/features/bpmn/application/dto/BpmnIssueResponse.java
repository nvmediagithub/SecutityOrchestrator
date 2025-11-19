package org.example.features.bpmn.application.dto;

import org.example.features.bpmn.domain.services.ProcessAnalyzer;

/**
 * DTO describing a single BPMN analysis issue.
 */
public record BpmnIssueResponse(
    String type,
    String severity,
    String description,
    String elementId,
    String suggestion
) {

    public static BpmnIssueResponse from(ProcessAnalyzer.ProcessIssue issue) {
        if (issue == null) {
            return new BpmnIssueResponse("UNKNOWN", "LOW", "No description", null, null);
        }
        return new BpmnIssueResponse(
            issue.getType(),
            issue.getSeverity(),
            issue.getDescription(),
            issue.getElementId(),
            issue.getSuggestion()
        );
    }
}
