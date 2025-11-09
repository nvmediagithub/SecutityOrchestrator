package org.example.infrastructure.services.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.entities.ApiSpecification;
import org.example.domain.entities.analysis.AnalysisResult;
import org.example.domain.valueobjects.WorkflowId;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main orchestrator for comprehensive API testing system
 * Coordinates OpenAPI analysis, BPMN integration, LLM analysis, and test execution
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApiTestingOrchestrator {

    private final OpenApiAnalysisService openApiService;
    private final BpmnTestingIntegrationService bpmnService;
    private final OwaspTestGenerator owaspTestGenerator;
    private final TestDataGenerator testDataGenerator;
    private final ExecutionTracker executionTracker;

    // In-memory storage for tracking execution status and results
    private final Map<String, ExecutionContext> executionContexts = new ConcurrentHashMap<>();

    /**
     * Analyze OpenAPI specification from URL or uploaded file
     */
    public CompletableFuture<AnalysisResult> analyzeOpenApiSpec(String sourceUrl, 
                                                               org.springframework.web.multipart.MultipartFile specFile,
                                                               String baseUrl) {
        String executionId = generateExecutionId();
        log.info("Starting OpenAPI analysis with execution ID: {}", executionId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                ExecutionContext context = new ExecutionContext(executionId, "OPENAPI_ANALYSIS");
                executionContexts.put(executionId, context);
                
                context.setStatus("STARTED");
                context.setProgress(0);
                
                // Parse OpenAPI specification
                AnalysisResult result = openApiService.parseAndAnalyze(sourceUrl, specFile, baseUrl);
                
                context.setStatus("COMPLETED");
                context.setProgress(100);
                context.setResult(Map.of("analysisResult", result));
                
                return result;
                
            } catch (Exception e) {
                log.error("Error analyzing OpenAPI specification", e);
                ExecutionContext context = executionContexts.get(executionId);
                if (context != null) {
                    context.setStatus("ERROR");
                    context.setError(e.getMessage());
                }
                throw new RuntimeException("Failed to analyze OpenAPI specification", e);
            }
        });
    }

    /**
     * Analyze BPMN process diagram
     */
    public CompletableFuture<AnalysisResult> analyzeBpmnProcess(org.springframework.web.multipart.MultipartFile bpmnFile) {
        String executionId = generateExecutionId();
        log.info("Starting BPMN analysis with execution ID: {}", executionId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                ExecutionContext context = new ExecutionContext(executionId, "BPMN_ANALYSIS");
                executionContexts.put(executionId, context);
                
                context.setStatus("STARTED");
                context.setProgress(0);
                
                // Parse and analyze BPMN
                AnalysisResult result = bpmnService.parseAndAnalyzeBpmn(bpmnFile);
                
                context.setStatus("COMPLETED");
                context.setProgress(100);
                context.setResult(Map.of("analysisResult", result));
                
                return result;
                
            } catch (Exception e) {
                log.error("Error analyzing BPMN process", e);
                ExecutionContext context = executionContexts.get(executionId);
                if (context != null) {
                    context.setStatus("ERROR");
                    context.setError(e.getMessage());
                }
                throw new RuntimeException("Failed to analyze BPMN process", e);
            }
        });
    }

    /**
     * Generate OWASP API security tests based on OpenAPI and BPMN analysis
     */
    public CompletableFuture<Map<String, Object>> generateOwaspSecurityTests(String openApiId, 
                                                                           String bpmnId, 
                                                                           List<String> owaspCategories) {
        String executionId = generateExecutionId();
        log.info("Generating OWASP tests with execution ID: {}", executionId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                ExecutionContext context = new ExecutionContext(executionId, "OWASP_TEST_GENERATION");
                executionContexts.put(executionId, context);
                
                context.setStatus("STARTED");
                context.setProgress(0);
                
                Map<String, Object> result = new HashMap<>();
                
                // Generate test cases based on OWASP categories
                result.put("testCases", owaspTestGenerator.generateSecurityTests(openApiId, owaspCategories));
                result.put("testScenarios", owaspTestGenerator.generateTestScenarios(openApiId, bpmnId));
                result.put("executionId", executionId);
                result.put("timestamp", new Date());
                
                context.setStatus("COMPLETED");
                context.setProgress(100);
                context.setResult(result);
                
                return result;
                
            } catch (Exception e) {
                log.error("Error generating OWASP tests", e);
                ExecutionContext context = executionContexts.get(executionId);
                if (context != null) {
                    context.setStatus("ERROR");
                    context.setError(e.getMessage());
                }
                throw new RuntimeException("Failed to generate OWASP tests", e);
            }
        });
    }

    /**
     * Generate context-aware test data considering dependencies between API steps
     */
    public CompletableFuture<Map<String, Object>> generateContextAwareTestData(String testCaseId,
                                                                              String bpmnId,
                                                                              Map<String, Object> constraints) {
        String executionId = generateExecutionId();
        log.info("Generating test data with execution ID: {}", executionId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                ExecutionContext context = new ExecutionContext(executionId, "TEST_DATA_GENERATION");
                executionContexts.put(executionId, context);
                
                context.setStatus("STARTED");
                context.setProgress(0);
                
                Map<String, Object> result = new HashMap<>();
                
                // Generate data considering BPMN workflow dependencies
                result.put("testData", testDataGenerator.generateContextAwareData(testCaseId, bpmnId, constraints));
                result.put("dataFlowChain", testDataGenerator.analyzeDataDependencies(bpmnId));
                result.put("generatedAt", new Date());
                result.put("executionId", executionId);
                
                context.setStatus("COMPLETED");
                context.setProgress(100);
                context.setResult(result);
                
                return result;
                
            } catch (Exception e) {
                log.error("Error generating test data", e);
                ExecutionContext context = executionContexts.get(executionId);
                if (context != null) {
                    context.setStatus("ERROR");
                    context.setError(e.getMessage());
                }
                throw new RuntimeException("Failed to generate test data", e);
            }
        });
    }

    /**
     * Execute end-to-end API testing workflow
     */
    public CompletableFuture<Map<String, Object>> executeEndToEndTesting(String openApiId,
                                                                        String bpmnId,
                                                                        Map<String, Object> testConfig) {
        String executionId = generateExecutionId();
        log.info("Starting end-to-end testing with execution ID: {}", executionId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                ExecutionContext context = new ExecutionContext(executionId, "E2E_TESTING");
                executionContexts.put(executionId, context);
                
                context.setStatus("STARTED");
                context.setProgress(0);
                
                Map<String, Object> result = new HashMap<>();
                
                // Step 1: Validate OpenAPI and BPMN compatibility
                context.setProgress(20);
                context.setCurrentStep("Validating specifications");
                validateSpecifications(openApiId, bpmnId);
                
                // Step 2: Generate test cases
                context.setProgress(40);
                context.setCurrentStep("Generating test cases");
                Map<String, Object> testCases = owaspTestGenerator.generateSecurityTests(openApiId, 
                    Arrays.asList("API1", "API2", "API3", "API4", "API5", "API6", "API7", "API8", "API9", "API10"));
                
                // Step 3: Generate test data
                context.setProgress(60);
                context.setCurrentStep("Generating test data");
                Map<String, Object> testData = testDataGenerator.generateContextAwareData(
                    "e2e_test_case", bpmnId, testConfig);
                
                // Step 4: Execute tests
                context.setProgress(80);
                context.setCurrentStep("Executing tests");
                Map<String, Object> executionResults = executionTracker.executeTests(
                    executionId, testCases, testData, testConfig);
                
                // Step 5: Generate report
                context.setProgress(95);
                context.setCurrentStep("Generating report");
                result.put("executionId", executionId);
                result.put("testCases", testCases);
                result.put("testData", testData);
                result.put("executionResults", executionResults);
                result.put("status", "COMPLETED");
                result.put("timestamp", new Date());
                
                context.setStatus("COMPLETED");
                context.setProgress(100);
                context.setCurrentStep("Completed");
                context.setResult(result);
                
                return result;
                
            } catch (Exception e) {
                log.error("Error in end-to-end testing", e);
                ExecutionContext context = executionContexts.get(executionId);
                if (context != null) {
                    context.setStatus("ERROR");
                    context.setError(e.getMessage());
                }
                throw new RuntimeException("Failed to execute end-to-end testing", e);
            }
        });
    }

    /**
     * Get current execution status
     */
    public Optional<Map<String, Object>> getExecutionStatus(String executionId) {
        ExecutionContext context = executionContexts.get(executionId);
        if (context == null) {
            return Optional.empty();
        }
        
        return Optional.of(Map.of(
            "executionId", executionId,
            "status", context.getStatus(),
            "progress", context.getProgress(),
            "currentStep", context.getCurrentStep(),
            "startedAt", context.getStartedAt(),
            "error", context.getError()
        ));
    }

    /**
     * Get complete execution results
     */
    public CompletableFuture<Map<String, Object>> getExecutionResults(String executionId) {
        return CompletableFuture.supplyAsync(() -> {
            ExecutionContext context = executionContexts.get(executionId);
            if (context == null) {
                throw new RuntimeException("Execution not found: " + executionId);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("executionId", executionId);
            result.put("summary", context.getResult());
            result.put("status", context.getStatus());
            result.put("duration", System.currentTimeMillis() - context.getStartedAt().getTime());
            
            return result;
        });
    }

    /**
     * Get available OWASP API Security categories
     */
    public Map<String, List<String>> getOwaspCategories() {
        return Map.of(
            "API1", List.of("Broken Object Level Authorization", "BOLA"),
            "API2", List.of("Broken User Authentication", "BUA"),
            "API3", List.of("Broken Object Property Level Authorization", "BOPLA"),
            "API4", List.of("Unrestricted Resource Consumption", "URC"),
            "API5", List.of("Broken Function Level Authorization", "BFLA"),
            "API6", List.of("Unrestricted Access to Sensitive Business Flows", "UASBF"),
            "API7", List.of("Server Side Request Forgery", "SSRF"),
            "API8", List.of("Security Misconfiguration", "SM"),
            "API9", List.of("Improper Inventory Management", "IIM"),
            "API10", List.of("Unsafe Consumption of APIs", "UCA")
        );
    }

    private String generateExecutionId() {
        return "exec_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private void validateSpecifications(String openApiId, String bpmnId) {
        // Validate that both OpenAPI and BPMN specifications are available
        // This is a placeholder - implement actual validation logic
    }

    /**
     * Internal class to track execution context
     */
    private static class ExecutionContext {
        private final String executionId;
        private final String type;
        private final Date startedAt = new Date();
        private String status;
        private int progress;
        private String currentStep;
        private String error;
        private Map<String, Object> result;

        public ExecutionContext(String executionId, String type) {
            this.executionId = executionId;
            this.type = type;
            this.status = "INITIALIZING";
            this.progress = 0;
            this.currentStep = "Starting";
        }

        // Getters and setters
        public String getExecutionId() { return executionId; }
        public String getType() { return type; }
        public Date getStartedAt() { return startedAt; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getProgress() { return progress; }
        public void setProgress(int progress) { this.progress = progress; }
        public String getCurrentStep() { return currentStep; }
        public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public Map<String, Object> getResult() { return result; }
        public void setResult(Map<String, Object> result) { this.result = result; }
    }
}