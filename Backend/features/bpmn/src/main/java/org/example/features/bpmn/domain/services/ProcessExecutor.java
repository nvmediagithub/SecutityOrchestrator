package org.example.features.bpmn.domain.services;

import org.example.features.bpmn.domain.entities.BpmnDiagram;
import org.example.shared.core.valueobjects.ModelId;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Domain service for executing BPMN processes
 */
public interface ProcessExecutor {

    /**
     * Starts execution of a BPMN process
     * @param diagram The BPMN diagram to execute
     * @param executionContext Context variables for execution
     * @return Execution result future
     */
    CompletableFuture<ExecutionResult> executeProcess(BpmnDiagram diagram, Map<String, Object> executionContext);

    /**
     * Validates if a BPMN process can be executed
     * @param diagram The BPMN diagram to validate
     * @return Validation result
     */
    ExecutionValidation validateExecution(BpmnDiagram diagram);

    /**
     * Gets the current execution status
     * @param executionId The execution identifier
     * @return Current execution status
     */
    ExecutionStatus getExecutionStatus(String executionId);

    /**
     * Cancels a running process execution
     * @param executionId The execution identifier
     * @return true if successfully cancelled
     */
    boolean cancelExecution(String executionId);

    /**
     * Result of process execution
     */
    class ExecutionResult {
        private final ModelId diagramId;
        private final String executionId;
        private final boolean success;
        private final Object result;
        private final String errorMessage;
        private final long executionTimeMs;

        public ExecutionResult(ModelId diagramId, String executionId, boolean success,
                             Object result, String errorMessage, long executionTimeMs) {
            this.diagramId = diagramId;
            this.executionId = executionId;
            this.success = success;
            this.result = result;
            this.errorMessage = errorMessage;
            this.executionTimeMs = executionTimeMs;
        }

        // Getters
        public ModelId getDiagramId() { return diagramId; }
        public String getExecutionId() { return executionId; }
        public boolean isSuccess() { return success; }
        public Object getResult() { return result; }
        public String getErrorMessage() { return errorMessage; }
        public long getExecutionTimeMs() { return executionTimeMs; }
    }

    /**
     * Validation result for process execution
     */
    class ExecutionValidation {
        private final boolean valid;
        private final String errorMessage;
        private final String validationDetails;

        public ExecutionValidation(boolean valid, String errorMessage, String validationDetails) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.validationDetails = validationDetails;
        }

        // Getters
        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
        public String getValidationDetails() { return validationDetails; }
    }

    /**
     * Status of process execution
     */
    enum ExecutionStatus {
        PENDING,
        RUNNING,
        COMPLETED,
        FAILED,
        CANCELLED
    }
}