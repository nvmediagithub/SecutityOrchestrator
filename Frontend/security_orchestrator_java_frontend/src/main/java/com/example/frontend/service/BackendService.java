package com.example.frontend.service;

import com.example.frontend.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class BackendService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${backend.url}")
    private String backendUrl;

    public BackendService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // Process endpoints
    public List<ProcessDto> getProcesses() {
        String url = backendUrl + "/api/v1/processes";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        try {
            ApiResponseDto<List<ProcessDto>> apiResponse = objectMapper.readValue(response.getBody(),
                new TypeReference<ApiResponseDto<List<ProcessDto>>>() {});
            if (apiResponse != null && apiResponse.isSuccess()) {
                return apiResponse.getData();
            }
        } catch (Exception e) {
            // Log error
        }
        return List.of();
    }

    public ProcessDto getProcess(String processId) {
        String url = backendUrl + "/api/v1/processes/" + processId;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        try {
            ApiResponseDto<ProcessDto> apiResponse = objectMapper.readValue(response.getBody(),
                new TypeReference<ApiResponseDto<ProcessDto>>() {});
            if (apiResponse != null && apiResponse.isSuccess()) {
                return apiResponse.getData();
            }
        } catch (Exception e) {
            // Log error
        }
        return null;
    }

    public ProcessDto createProcess(MultipartFile file, String name) throws IOException {
        String url = backendUrl + "/api/v1/processes";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        if (name != null) {
            body.add("name", name);
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        try {
            ApiResponseDto<ProcessDto> apiResponse = objectMapper.readValue(response.getBody(),
                new TypeReference<ApiResponseDto<ProcessDto>>() {});
            if (apiResponse != null && apiResponse.isSuccess()) {
                return apiResponse.getData();
            }
        } catch (Exception e) {
            // Log error
        }
        return null;
    }

    // Workflow endpoints
    public List<WorkflowDto> getWorkflows() {
        String url = backendUrl + "/api/v1/workflows";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        try {
            ApiResponseDto<List<WorkflowDto>> apiResponse = objectMapper.readValue(response.getBody(),
                new TypeReference<ApiResponseDto<List<WorkflowDto>>>() {});
            if (apiResponse != null && apiResponse.isSuccess()) {
                return apiResponse.getData();
            }
        } catch (Exception e) {
            // Log error
        }
        return List.of();
    }

    public WorkflowDto getWorkflow(String workflowId) {
        String url = backendUrl + "/api/v1/workflows/" + workflowId;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        try {
            ApiResponseDto<WorkflowDto> apiResponse = objectMapper.readValue(response.getBody(),
                new TypeReference<ApiResponseDto<WorkflowDto>>() {});
            if (apiResponse != null && apiResponse.isSuccess()) {
                return apiResponse.getData();
            }
        } catch (Exception e) {
            // Log error
        }
        return null;
    }

    public WorkflowDto createWorkflow(String name, String processId, List<String> testCaseIds) {
        String url = backendUrl + "/api/v1/workflows";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        WorkflowCreationRequest request = new WorkflowCreationRequest();
        request.setName(name);
        request.setProcessId(processId);
        request.setTestCaseIds(testCaseIds);

        HttpEntity<WorkflowCreationRequest> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        try {
            ApiResponseDto<WorkflowDto> apiResponse = objectMapper.readValue(response.getBody(),
                new TypeReference<ApiResponseDto<WorkflowDto>>() {});
            if (apiResponse != null && apiResponse.isSuccess()) {
                return apiResponse.getData();
            }
        } catch (Exception e) {
            // Log error
        }
        return null;
    }

    public ExecutionDto executeWorkflow(String workflowId) {
        String url = backendUrl + "/api/v1/workflows/" + workflowId + "/execute";

        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

        try {
            ApiResponseDto<ExecutionStartedResponse> apiResponse = objectMapper.readValue(response.getBody(),
                new TypeReference<ApiResponseDto<ExecutionStartedResponse>>() {});
            if (apiResponse != null && apiResponse.isSuccess()) {
                ExecutionStartedResponse started = apiResponse.getData();
                // Get execution status
                return getExecutionStatus(workflowId);
            }
        } catch (Exception e) {
            // Log error
        }
        return null;
    }

    public ExecutionDto getExecutionStatus(String workflowId) {
        String url = backendUrl + "/api/v1/workflows/" + workflowId + "/status";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        try {
            ApiResponseDto<ExecutionStatusResponse> apiResponse = objectMapper.readValue(response.getBody(),
                new TypeReference<ApiResponseDto<ExecutionStatusResponse>>() {});
            if (apiResponse != null && apiResponse.isSuccess()) {
                ExecutionStatusResponse statusResponse = apiResponse.getData();
                ExecutionDto execution = new ExecutionDto();
                execution.setExecutionId(statusResponse.getExecutionId());
                execution.setStatus(statusResponse.getStatus());
                execution.setMessage(statusResponse.getMessage());
                execution.setCompletedAt(statusResponse.getCompletedAt());
                execution.setWorkflowId(workflowId);
                return execution;
            }
        } catch (Exception e) {
            // Log error
        }
        return null;
    }

    // Inner classes for requests
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

    public static class ExecutionStartedResponse {
        private String executionId;
        private String status;
        private String statusUrl;

        public String getExecutionId() { return executionId; }
        public void setExecutionId(String executionId) { this.executionId = executionId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getStatusUrl() { return statusUrl; }
        public void setStatusUrl(String statusUrl) { this.statusUrl = statusUrl; }
    }

    public static class ExecutionStatusResponse {
        private String executionId;
        private String status;
        private String message;
        private java.time.Instant completedAt;

        public String getExecutionId() { return executionId; }
        public void setExecutionId(String executionId) { this.executionId = executionId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public java.time.Instant getCompletedAt() { return completedAt; }
        public void setCompletedAt(java.time.Instant completedAt) { this.completedAt = completedAt; }
    }
}