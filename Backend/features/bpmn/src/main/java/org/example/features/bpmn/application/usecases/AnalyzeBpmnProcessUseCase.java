package org.example.features.bpmn.application.usecases;

import org.example.features.bpmn.domain.entities.BpmnDiagram;
import org.example.features.bpmn.domain.services.ProcessAnalyzer;
import org.example.shared.core.valueobjects.ModelId;

/**
 * Use case for analyzing a BPMN process for security and structural issues
 */
public class AnalyzeBpmnProcessUseCase {

    private final ProcessAnalyzer processAnalyzer;

    public AnalyzeBpmnProcessUseCase(ProcessAnalyzer processAnalyzer) {
        this.processAnalyzer = processAnalyzer;
    }

    /**
     * Analyzes a BPMN diagram comprehensively
     * @param diagramId The ID of the diagram to analyze
     * @param diagram The BPMN diagram to analyze
     * @return Analysis result
     */
    public ProcessAnalyzer.AnalysisResult analyzeBpmnProcess(ModelId diagramId, BpmnDiagram diagram) {
        // Perform comprehensive analysis
        return processAnalyzer.analyzeComprehensive(diagram);
    }

    /**
     * Analyzes BPMN diagram for structural issues only
     * @param diagramId The ID of the diagram to analyze
     * @param diagram The BPMN diagram to analyze
     * @return List of structural issues
     */
    public java.util.List<ProcessAnalyzer.ProcessIssue> analyzeStructure(ModelId diagramId, BpmnDiagram diagram) {
        return processAnalyzer.analyzeStructure(diagram);
    }

    /**
     * Analyzes BPMN diagram for security issues only
     * @param diagramId The ID of the diagram to analyze
     * @param diagram The BPMN diagram to analyze
     * @return List of security issues
     */
    public java.util.List<ProcessAnalyzer.ProcessIssue> analyzeSecurity(ModelId diagramId, BpmnDiagram diagram) {
        return processAnalyzer.analyzeSecurity(diagram);
    }

    /**
     * Analyzes BPMN diagram for performance issues only
     * @param diagramId The ID of the diagram to analyze
     * @param diagram The BPMN diagram to analyze
     * @return List of performance issues
     */
    public java.util.List<ProcessAnalyzer.ProcessIssue> analyzePerformance(ModelId diagramId, BpmnDiagram diagram) {
        return processAnalyzer.analyzePerformance(diagram);
    }
}