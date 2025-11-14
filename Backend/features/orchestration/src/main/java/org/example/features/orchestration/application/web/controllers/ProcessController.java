package org.example.features.orchestration.application.web.controllers;

import org.example.domain.dto.ApiResponse;
import org.example.domain.dto.ProcessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for managing processes
 * Provides CRUD operations for BPMN processes
 */
@RestController
@RequestMapping("/api/processes")
public class ProcessController {

    /**
     * Create a new process from BPMN file
     * POST /api/processes
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProcessResponse>> createProcess(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name) {

        // Mock implementation - return stub data
        ProcessResponse process = new ProcessResponse(
            "mock-process-" + UUID.randomUUID().toString().substring(0, 8),
            name != null ? name : "Mock Process",
            "1.0.0",
            "ACTIVE",
            java.time.Instant.now(),
            java.time.Instant.now()
        );

        return ResponseEntity.ok(ApiResponse.success(process, UUID.randomUUID().toString()));
    }

    /**
     * List all processes
     * GET /api/processes
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProcessResponse>>> listProcesses(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        // Mock data
        List<ProcessResponse> processes = Arrays.asList(
            new ProcessResponse("mock-process-1", "Process 1", "1.0.0", "ACTIVE",
                java.time.Instant.now(), java.time.Instant.now()),
            new ProcessResponse("mock-process-2", "Process 2", "1.0.0", "INACTIVE",
                java.time.Instant.now(), java.time.Instant.now())
        );

        return ResponseEntity.ok(ApiResponse.success(processes, UUID.randomUUID().toString()));
    }

    /**
     * Get process by ID
     * GET /api/processes/{processId}
     */
    @GetMapping("/{processId}")
    public ResponseEntity<ApiResponse<ProcessResponse>> getProcess(@PathVariable String processId) {
        // Mock data
        ProcessResponse process = new ProcessResponse(
            processId,
            "Mock Process",
            "1.0.0",
            "ACTIVE",
            java.time.Instant.now(),
            java.time.Instant.now()
        );

        return ResponseEntity.ok(ApiResponse.success(process, UUID.randomUUID().toString()));
    }

    /**
     * Update process
     * PUT /api/processes/{processId}
     */
    @PutMapping("/{processId}")
    public ResponseEntity<ApiResponse<ProcessResponse>> updateProcess(
            @PathVariable String processId,
            @RequestBody ProcessUpdateRequest request) {

        // Mock data
        ProcessResponse process = new ProcessResponse(
            processId,
            request.getName() != null ? request.getName() : "Updated Process",
            "1.0.1",
            "ACTIVE",
            java.time.Instant.now(),
            java.time.Instant.now()
        );

        return ResponseEntity.ok(ApiResponse.success(process, UUID.randomUUID().toString()));
    }

    /**
     * Delete process
     * DELETE /api/processes/{processId}
     */
    @DeleteMapping("/{processId}")
    public ResponseEntity<Void> deleteProcess(@PathVariable String processId) {
        return ResponseEntity.noContent().build();
    }

    /**
     * Execute process
     * POST /api/processes/{processId}/execute
     */
    @PostMapping("/{processId}/execute")
    public ResponseEntity<ApiResponse<ExecutionResultResponse>> executeProcess(
            @PathVariable String processId,
            @RequestBody(required = false) ProcessExecutionRequest request) {

        // Mock execution result
        ExecutionResultResponse result = new ExecutionResultResponse(
            "exec-" + UUID.randomUUID().toString().substring(0, 8),
            "COMPLETED",
            "Process executed successfully"
        );

        return ResponseEntity.ok(ApiResponse.success(result, UUID.randomUUID().toString()));
    }

    // Inner DTOs for requests
    public static class ProcessUpdateRequest {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class ProcessExecutionRequest {
        private Object variables;
        public Object getVariables() { return variables; }
        public void setVariables(Object variables) { this.variables = variables; }
    }

    public static class ExecutionResultResponse {
        private String executionId;
        private String status;
        private String message;

        public ExecutionResultResponse(String executionId, String status, String message) {
            this.executionId = executionId;
            this.status = status;
            this.message = message;
        }

        // Getters
        public String getExecutionId() { return executionId; }
        public String getStatus() { return status; }
        public String getMessage() { return message; }
    }
}