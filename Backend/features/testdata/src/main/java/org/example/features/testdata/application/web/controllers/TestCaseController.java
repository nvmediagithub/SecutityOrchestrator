package org.example.features.testdata.application.web.controllers;

import org.example.domain.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for managing test cases
 * Provides operations for executing test cases
 */
@RestController
@RequestMapping("/api/v1/test-cases")
public class TestCaseController {

    /**
     * Execute a test case
     * POST /api/v1/test-cases/{testCaseId}/execute
     */
    @PostMapping("/{testCaseId}/execute")
    public ResponseEntity<ApiResponse<TestResultResponse>> executeTestCase(
            @PathVariable String testCaseId,
            @RequestBody(required = false) TestExecutionRequest request) {

        // Mock execution result
        TestResultResponse result = new TestResultResponse(
            "exec-" + UUID.randomUUID().toString().substring(0, 8),
            "PASSED",
            200,
            "{\"message\": \"Success\"}",
            java.time.Duration.ofMillis(150),
            java.time.Instant.now()
        );

        return ResponseEntity.ok(ApiResponse.success(result, UUID.randomUUID().toString()));
    }

    // Inner DTOs
    public static class TestExecutionRequest {
        private Object testData;
        public Object getTestData() { return testData; }
        public void setTestData(Object testData) { this.testData = testData; }
    }

    public static class TestResultResponse {
        private String executionId;
        private String status;
        private int actualStatusCode;
        private String responseBody;
        private java.time.Duration executionTime;
        private java.time.Instant executedAt;

        public TestResultResponse(String executionId, String status, int actualStatusCode,
                                String responseBody, java.time.Duration executionTime,
                                java.time.Instant executedAt) {
            this.executionId = executionId;
            this.status = status;
            this.actualStatusCode = actualStatusCode;
            this.responseBody = responseBody;
            this.executionTime = executionTime;
            this.executedAt = executedAt;
        }

        // Getters
        public String getExecutionId() { return executionId; }
        public String getStatus() { return status; }
        public int getActualStatusCode() { return actualStatusCode; }
        public String getResponseBody() { return responseBody; }
        public java.time.Duration getExecutionTime() { return executionTime; }
        public java.time.Instant getExecutedAt() { return executedAt; }
    }
}