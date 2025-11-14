package org.example.features.bpmn.domain.services;

import org.example.features.bpmn.domain.entities.BpmnDiagram;

/**
 * Domain service for parsing BPMN process definitions
 */
public interface ProcessParser {

    /**
     * Parses BPMN content and validates its structure
     * @param bpmnContent The BPMN XML content
     * @return Parsed and validated BpmnDiagram
     * @throws BpmnParseException if parsing fails
     */
    BpmnDiagram parseBpmnContent(String bpmnContent) throws BpmnParseException;

    /**
     * Validates BPMN content structure without full parsing
     * @param bpmnContent The BPMN XML content
     * @return true if valid BPMN structure
     */
    boolean validateBpmnStructure(String bpmnContent);

    /**
     * Extracts process ID from BPMN content
     * @param bpmnContent The BPMN XML content
     * @return Process ID or null if not found
     */
    String extractProcessId(String bpmnContent);

    /**
     * Extracts process name from BPMN content
     * @param bpmnContent The BPMN XML content
     * @return Process name or null if not found
     */
    String extractProcessName(String bpmnContent);

    /**
     * Exception thrown when BPMN parsing fails
     */
    class BpmnParseException extends RuntimeException {
        public BpmnParseException(String message) {
            super(message);
        }

        public BpmnParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}