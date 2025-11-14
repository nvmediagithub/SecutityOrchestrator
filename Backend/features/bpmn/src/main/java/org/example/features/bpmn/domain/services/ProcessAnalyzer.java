package org.example.features.bpmn.domain.services;

import org.example.features.bpmn.domain.entities.BpmnDiagram;
import org.example.shared.core.valueobjects.ModelId;

import java.util.List;

/**
 * Domain service for analyzing BPMN processes for security and structural issues
 */
public interface ProcessAnalyzer {

    /**
     * Analyzes a BPMN diagram for structural issues
     * @param diagram The BPMN diagram to analyze
     * @return List of structural issues found
     */
    List<ProcessIssue> analyzeStructure(BpmnDiagram diagram);

    /**
     * Analyzes a BPMN diagram for security vulnerabilities
     * @param diagram The BPMN diagram to analyze
     * @return List of security issues found
     */
    List<ProcessIssue> analyzeSecurity(BpmnDiagram diagram);

    /**
     * Analyzes a BPMN diagram for performance issues
     * @param diagram The BPMN diagram to analyze
     * @return List of performance issues found
     */
    List<ProcessIssue> analyzePerformance(BpmnDiagram diagram);

    /**
     * Performs comprehensive analysis combining all analysis types
     * @param diagram The BPMN diagram to analyze
     * @return Analysis result with all issue types
     */
    AnalysisResult analyzeComprehensive(BpmnDiagram diagram);

    /**
     * Represents a process analysis issue
     */
    interface ProcessIssue {
        String getType();
        String getSeverity();
        String getDescription();
        String getElementId();
        String getSuggestion();
    }

    /**
     * Result of comprehensive process analysis
     */
    class AnalysisResult {
        private final ModelId diagramId;
        private final List<ProcessIssue> structuralIssues;
        private final List<ProcessIssue> securityIssues;
        private final List<ProcessIssue> performanceIssues;
        private final int totalIssues;
        private final String overallRisk;

        public AnalysisResult(ModelId diagramId, List<ProcessIssue> structuralIssues,
                            List<ProcessIssue> securityIssues, List<ProcessIssue> performanceIssues) {
            this.diagramId = diagramId;
            this.structuralIssues = structuralIssues;
            this.securityIssues = securityIssues;
            this.performanceIssues = performanceIssues;
            this.totalIssues = structuralIssues.size() + securityIssues.size() + performanceIssues.size();
            this.overallRisk = calculateOverallRisk();
        }

        private String calculateOverallRisk() {
            int highSeverityCount = countHighSeverityIssues();
            if (highSeverityCount > 5) return "CRITICAL";
            if (highSeverityCount > 2) return "HIGH";
            if (totalIssues > 0) return "MEDIUM";
            return "LOW";
        }

        private int countHighSeverityIssues() {
            return (int) (structuralIssues.stream().filter(i -> "HIGH".equals(i.getSeverity())).count() +
                         securityIssues.stream().filter(i -> "HIGH".equals(i.getSeverity())).count() +
                         performanceIssues.stream().filter(i -> "HIGH".equals(i.getSeverity())).count());
        }

        // Getters
        public ModelId getDiagramId() { return diagramId; }
        public List<ProcessIssue> getStructuralIssues() { return structuralIssues; }
        public List<ProcessIssue> getSecurityIssues() { return securityIssues; }
        public List<ProcessIssue> getPerformanceIssues() { return performanceIssues; }
        public int getTotalIssues() { return totalIssues; }
        public String getOverallRisk() { return overallRisk; }
    }
}