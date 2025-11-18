package org.example.features.orchestration.application.web.controllers;

import org.example.shared.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for managing workflows
 * Provides operations for creating and executing workflows
 */
@RestController
@RequestMapping("/api/v1/workflows")
public class WorkflowController {

    /**
     * Create a new workflow
     * POST /api/v1/workflows
     */
    @PostMapping
    public ResponseEntity<ApiResponse<WorkflowResponse>> createWorkflow(@RequestBody WorkflowCreationRequest request) {
        // Mock implementation
        WorkflowResponse workflow = new WorkflowResponse(
            "mock-workflow-" + UUID.randomUUID().toString().substring(0, 8),
            request.getName(),
            "DRAFT",
            java.time.Instant.now(),
            java.time.Instant.now()
        );

        return ResponseEntity.ok(ApiResponse.success(workflow));
    }

    /**
     * List all workflows
     * GET /api/v1/workflows
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkflowSummary>>> listWorkflows(
            @RequestParam(value = "status", required = false) String status) {

        // Mock data
        List<WorkflowSummary> workflows = Arrays.asList(
            new WorkflowSummary("mock-workflow-1", "Test Workflow 1", "ACTIVE"),
            new WorkflowSummary("mock-workflow-2", "Test Workflow 2", "COMPLETED")
        );

        return ResponseEntity.ok(ApiResponse.success(workflows));
    }

    /**
     * Get workflow by ID
     * GET /api/v1/workflows/{workflowId}
     */
    @GetMapping("/{workflowId}")
    public ResponseEntity<ApiResponse<WorkflowResponse>> getWorkflow(@PathVariable String workflowId) {
        // Mock data
        WorkflowResponse workflow = new WorkflowResponse(
            workflowId,
            "Mock Workflow",
            "ACTIVE",
            java.time.Instant.now(),
            java.time.Instant.now()
        );

        return ResponseEntity.ok(ApiResponse.success(workflow));
    }

    /**
     * Execute workflow
     * POST /api/v1/workflows/{workflowId}/execute
     */
    @PostMapping("/{workflowId}/execute")
    public ResponseEntity<ApiResponse<ExecutionStartedResponse>> executeWorkflow(
            @PathVariable String workflowId,
            @RequestBody(required = false) WorkflowExecutionRequest request) {

        // Mock execution start
        ExecutionStartedResponse response = new ExecutionStartedResponse(
            "exec-" + UUID.randomUUID().toString().substring(0, 8),
            "RUNNING",
            "/api/v1/workflows/" + workflowId + "/status"
        );

        return ResponseEntity.accepted().body(ApiResponse.success(response));
    }

    /**
     * Get workflow execution status
     * GET /api/v1/workflows/{workflowId}/status
     */
    @GetMapping("/{workflowId}/status")
    public ResponseEntity<ApiResponse<ExecutionStatusResponse>> getExecutionStatus(@PathVariable String workflowId) {
        // Mock status
        ExecutionStatusResponse status = new ExecutionStatusResponse(
            "exec-123",
            "COMPLETED",
            "Workflow execution completed successfully",
            java.time.Instant.now()
        );

        return ResponseEntity.ok(ApiResponse.success(status));
    }

    // Inner DTOs
    public static class WorkflowCreationRequest {
        private String name;
        private String processId;
        private List<String> testCaseIds;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getProcessId() { return processId; }
        public void setProcessId(String processId) { this.processId = processId; }
        public List<String> getTestCaseIds() { return testCaseIds; }
        public void setTestCaseIds(List<String> testCaseIds) { this.testCaseIds = testCaseIds; }
    }

    public static class WorkflowResponse {
        private String id;
        private String name;
        private String status;
        private java.time.Instant createdAt;
        private java.time.Instant updatedAt;

        public WorkflowResponse(String id, String name, String status,
                              java.time.Instant createdAt, java.time.Instant updatedAt) {
            this.id = id;
            this.name = name;
            this.status = status;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getStatus() { return status; }
        public java.time.Instant getCreatedAt() { return createdAt; }
        public java.time.Instant getUpdatedAt() { return updatedAt; }
    }

    public static class WorkflowSummary {
        private String id;
        private String name;
        private String status;

        public WorkflowSummary(String id, String name, String status) {
            this.id = id;
            this.name = name;
            this.status = status;
        }

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getStatus() { return status; }
    }

    public static class WorkflowExecutionRequest {
        private Object variables;
        public Object getVariables() { return variables; }
        public void setVariables(Object variables) { this.variables = variables; }
    }

    public static class ExecutionStartedResponse {
        private String executionId;
        private String status;
        private String statusUrl;

        public ExecutionStartedResponse(String executionId, String status, String statusUrl) {
            this.executionId = executionId;
            this.status = status;
            this.statusUrl = statusUrl;
        }

        // Getters
        public String getExecutionId() { return executionId; }
        public String getStatus() { return status; }
        public String getStatusUrl() { return statusUrl; }
    }

    public static class ExecutionStatusResponse {
        private String executionId;
        private String status;
        private String message;
        private java.time.Instant completedAt;

        public ExecutionStatusResponse(String executionId, String status, String message, java.time.Instant completedAt) {
            this.executionId = executionId;
            this.status = status;
            this.message = message;
            this.completedAt = completedAt;
        }

        // Getters
        public String getExecutionId() { return executionId; }
        public String getStatus() { return status; }
        public String getMessage() { return message; }
        public java.time.Instant getCompletedAt() { return completedAt; }
    }
}