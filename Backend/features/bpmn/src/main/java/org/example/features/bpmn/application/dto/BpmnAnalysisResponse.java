package org.example.features.bpmn.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.features.bpmn.domain.entities.BpmnDiagram;
import org.example.features.bpmn.domain.services.ProcessAnalyzer;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO returned to frontend after BPMN analysis.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BpmnAnalysisResponse(
    String diagramName,
    String processId,
    String overallRisk,
    int totalIssues,
    List<BpmnIssueResponse> structuralIssues,
    List<BpmnIssueResponse> securityIssues,
    List<BpmnIssueResponse> performanceIssues,
    String bpmnContent,
    Instant analyzedAt
) {

    public static BpmnAnalysisResponse from(
        BpmnDiagram diagram,
        ProcessAnalyzer.AnalysisResult analysisResult,
        String originalContent
    ) {
        return new BpmnAnalysisResponse(
            diagram.getDiagramName(),
            diagram.getPrimaryProcessId(),
            analysisResult.getOverallRisk(),
            analysisResult.getTotalIssues(),
            mapIssues(analysisResult.getStructuralIssues()),
            mapIssues(analysisResult.getSecurityIssues()),
            mapIssues(analysisResult.getPerformanceIssues()),
            originalContent,
            Instant.now()
        );
    }

    private static List<BpmnIssueResponse> mapIssues(List<ProcessAnalyzer.ProcessIssue> issues) {
        return issues.stream()
            .map(BpmnIssueResponse::from)
            .collect(Collectors.toList());
    }
}
