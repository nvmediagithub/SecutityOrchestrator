package org.example.features.bpmn.application.usecases;

import org.example.features.bpmn.domain.entities.BpmnDiagram;
import org.example.features.bpmn.domain.services.ProcessAnalyzer;
import org.example.shared.core.valueobjects.ModelId;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Use case for generating BPMN analysis reports
 */
public class GenerateBpmnReportUseCase {

    private final ProcessAnalyzer processAnalyzer;

    public GenerateBpmnReportUseCase(ProcessAnalyzer processAnalyzer) {
        this.processAnalyzer = processAnalyzer;
    }

    /**
     * Generates a comprehensive BPMN analysis report
     * @param diagramId The ID of the diagram
     * @param diagram The BPMN diagram
     * @return Analysis report
     */
    public BpmnReport generateReport(ModelId diagramId, BpmnDiagram diagram) {
        // Perform comprehensive analysis
        ProcessAnalyzer.AnalysisResult analysisResult = processAnalyzer.analyzeComprehensive(diagram);

        return new BpmnReport(
            diagramId,
            diagram.getDiagramName(),
            LocalDateTime.now(),
            analysisResult,
            generateSummary(analysisResult),
            generateRecommendations(analysisResult)
        );
    }

    /**
     * Generates a summary of the analysis results
     */
    private String generateSummary(ProcessAnalyzer.AnalysisResult result) {
        StringBuilder summary = new StringBuilder();
        summary.append("Analysis Summary: ");
        summary.append(result.getTotalIssues()).append(" issues found. ");
        summary.append("Risk Level: ").append(result.getOverallRisk());

        if (result.getStructuralIssues().size() > 0) {
            summary.append(" | Structural Issues: ").append(result.getStructuralIssues().size());
        }
        if (result.getSecurityIssues().size() > 0) {
            summary.append(" | Security Issues: ").append(result.getSecurityIssues().size());
        }
        if (result.getPerformanceIssues().size() > 0) {
            summary.append(" | Performance Issues: ").append(result.getPerformanceIssues().size());
        }

        return summary.toString();
    }

    /**
     * Generates recommendations based on analysis results
     */
    private List<String> generateRecommendations(ProcessAnalyzer.AnalysisResult result) {
        java.util.List<String> recommendations = new java.util.ArrayList<>();

        if (result.getOverallRisk().equals("CRITICAL")) {
            recommendations.add("Critical issues detected - immediate review required");
        }

        if (result.getSecurityIssues().size() > 0) {
            recommendations.add("Address security vulnerabilities before deployment");
        }

        if (result.getStructuralIssues().size() > 0) {
            recommendations.add("Fix structural issues to ensure proper process flow");
        }

        if (result.getPerformanceIssues().size() > 0) {
            recommendations.add("Optimize process performance bottlenecks");
        }

        if (recommendations.isEmpty()) {
            recommendations.add("Process appears to be well-structured and secure");
        }

        return recommendations;
    }

    /**
     * BPMN Analysis Report
     */
    public static class BpmnReport {
        private final ModelId diagramId;
        private final String diagramName;
        private final LocalDateTime generatedAt;
        private final ProcessAnalyzer.AnalysisResult analysisResult;
        private final String summary;
        private final List<String> recommendations;

        public BpmnReport(ModelId diagramId, String diagramName, LocalDateTime generatedAt,
                         ProcessAnalyzer.AnalysisResult analysisResult, String summary,
                         List<String> recommendations) {
            this.diagramId = diagramId;
            this.diagramName = diagramName;
            this.generatedAt = generatedAt;
            this.analysisResult = analysisResult;
            this.summary = summary;
            this.recommendations = recommendations;
        }

        // Getters
        public ModelId getDiagramId() { return diagramId; }
        public String getDiagramName() { return diagramName; }
        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public ProcessAnalyzer.AnalysisResult getAnalysisResult() { return analysisResult; }
        public String getSummary() { return summary; }
        public List<String> getRecommendations() { return recommendations; }
    }
}