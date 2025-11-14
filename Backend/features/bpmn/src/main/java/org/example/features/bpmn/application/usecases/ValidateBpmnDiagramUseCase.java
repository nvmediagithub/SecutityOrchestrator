package org.example.features.bpmn.application.usecases;

import org.example.features.bpmn.domain.entities.BpmnDiagram;
import org.example.features.bpmn.domain.services.ProcessParser;
import org.example.shared.core.valueobjects.ModelId;

import java.util.ArrayList;
import java.util.List;

/**
 * Use case for validating BPMN diagrams
 */
public class ValidateBpmnDiagramUseCase {

    private final ProcessParser processParser;

    public ValidateBpmnDiagramUseCase(ProcessParser processParser) {
        this.processParser = processParser;
    }

    /**
     * Validates a BPMN diagram comprehensively
     * @param diagram The BPMN diagram to validate
     * @return Validation result
     */
    public ValidationResult validateDiagram(BpmnDiagram diagram) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // Validate basic structure
        if (!diagram.isValid()) {
            errors.add("Diagram is missing required fields (name or content)");
        }

        // Validate BPMN structure
        if (!processParser.validateBpmnStructure(diagram.getBpmnContent())) {
            errors.add("Invalid BPMN XML structure");
        }

        // Additional validations can be added here
        if (diagram.getBpmnContent().length() > 10 * 1024 * 1024) { // 10MB limit
            errors.add("BPMN content exceeds maximum size limit");
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Validates BPMN content only
     * @param bpmnContent The BPMN XML content
     * @return Simple validation result
     */
    public boolean validateBpmnContent(String bpmnContent) {
        return processParser.validateBpmnStructure(bpmnContent);
    }

    /**
     * Result of diagram validation
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final List<String> warnings;

        public ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
            this.valid = valid;
            this.errors = errors;
            this.warnings = warnings;
        }

        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }
        public List<String> getWarnings() { return warnings; }
        public int getTotalIssues() { return errors.size() + warnings.size(); }
    }
}